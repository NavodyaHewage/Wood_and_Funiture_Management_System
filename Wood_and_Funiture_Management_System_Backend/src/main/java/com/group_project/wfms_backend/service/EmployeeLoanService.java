package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.EmployeeLoanDTO;
import com.group_project.wfms_backend.model.Employee_loan;
import com.group_project.wfms_backend.model.Employee;
import com.group_project.wfms_backend.model.EmployeeSalaryRate;
import com.group_project.wfms_backend.repository.EmployeeRepository;
import com.group_project.wfms_backend.repository.Employeeloanrepository;
import com.group_project.wfms_backend.repository.EmployeeSalaryRateRepository;
import com.group_project.wfms_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeLoanService {

    @Autowired
    private Employeeloanrepository loanRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeSalaryRateRepository salaryRateRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    // READ ALL - Using JdbcTemplate to bypass "Unknown column 'Balance'" error
    public List<EmployeeLoanDTO> getAllLoans() {
        String sql = "SELECT l.Loan_ID, l.Employee_id, e.Full_Name as Employee_Name, l.Loan_Amount, l.Issued_Date, l.Reason, l.Total_Deducted, l.Status, l.Remarks " +
                     "FROM Employee_loan l " +
                     "JOIN Employee e ON l.Employee_id = e.Id";
        
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            EmployeeLoanDTO dto = new EmployeeLoanDTO();
            dto.setLoanId(rs.getInt("Loan_ID"));
            dto.setEmployeeId(rs.getInt("Employee_id"));
            dto.setEmployeeName(rs.getString("Employee_Name"));
            
            BigDecimal amount = rs.getBigDecimal("Loan_Amount");
            dto.setLoanAmount(amount);
            
            java.sql.Date sqlDate = rs.getDate("Issued_Date");
            dto.setIssuedDate(sqlDate != null ? sqlDate.toLocalDate() : null);
            
            dto.setReason(rs.getString("Reason"));
            
            BigDecimal totalDeducted = rs.getBigDecimal("Total_Deducted");
            dto.setTotalDeducted(totalDeducted != null ? totalDeducted : BigDecimal.ZERO);
            
            // Manually calculate balance to avoid DB column dependency
            dto.setBalance(amount.subtract(dto.getTotalDeducted()));
            
            dto.setStatus(rs.getString("Status"));
            dto.setRemarks(rs.getString("Remarks"));
            return dto;
        });
    }

    // READ BY ID
    public EmployeeLoanDTO getLoanById(Integer id) {
        String sql = "SELECT l.Loan_ID, l.Employee_id, e.Full_Name as Employee_Name, l.Loan_Amount, l.Issued_Date, l.Reason, l.Total_Deducted, l.Status, l.Remarks " +
                     "FROM Employee_loan l " +
                     "JOIN Employee e ON l.Employee_id = e.Id " +
                     "WHERE l.Loan_ID = ?";
        
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            EmployeeLoanDTO dto = new EmployeeLoanDTO();
            dto.setLoanId(rs.getInt("Loan_ID"));
            dto.setEmployeeId(rs.getInt("Employee_id"));
            dto.setEmployeeName(rs.getString("Employee_Name"));
            BigDecimal amount = rs.getBigDecimal("Loan_Amount");
            dto.setLoanAmount(amount);
            java.sql.Date sqlDate = rs.getDate("Issued_Date");
            dto.setIssuedDate(sqlDate != null ? sqlDate.toLocalDate() : null);
            dto.setReason(rs.getString("Reason"));
            BigDecimal totalDeducted = rs.getBigDecimal("Total_Deducted");
            dto.setTotalDeducted(totalDeducted != null ? totalDeducted : BigDecimal.ZERO);
            dto.setBalance(amount.subtract(dto.getTotalDeducted()));
            dto.setStatus(rs.getString("Status"));
            dto.setRemarks(rs.getString("Remarks"));
            return dto;
        }, id);
    }

    // CREATE
    @Transactional
    public EmployeeLoanDTO createLoan(EmployeeLoanDTO dto) {
        Employee_loan loan = new Employee_loan();

        // Map simple fields
        loan.setLoanAmount(dto.getLoanAmount());
        loan.setIssuedDate(dto.getIssuedDate());
        loan.setReason(dto.getReason());
        loan.setRemarks(dto.getRemarks());
        loan.setTotalDeducted(BigDecimal.ZERO);
        loan.setStatus(com.group_project.wfms_backend.model.LoanStatus.ACTIVE);

        // Resolve Relationships
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        // BUSINESS RULE: Cannot have multiple active loans simultaneously
        boolean hasActive = loanRepository.findAll().stream()
                .filter(l -> l.getEmployee().getId().equals(employee.getId()))
                .anyMatch(l -> l.getStatus() != com.group_project.wfms_backend.model.LoanStatus.SETTLED);
        
        if (hasActive) {
            throw new RuntimeException("Employee already has an active or partially paid loan.");
        }

        loan.setEmployee(employee);

        if (dto.getCreatedById() != null) {
            loan.setCreatedBy(userRepository.findById(dto.getCreatedById()).orElse(null));
        }

        // Hibernate save works as long as 'balance' is insertable=false
        Employee_loan savedLoan = loanRepository.save(loan);
        
        // Return DTO with manually calculated balance
        EmployeeLoanDTO result = convertToDTO(savedLoan);
        result.setEmployeeName(employee.getFullName());
        result.setBalance(savedLoan.getLoanAmount()); // Initial balance is full amount
        return result;
    }

    // UPDATE
    @Transactional
    public EmployeeLoanDTO updateLoan(Integer id, EmployeeLoanDTO dto) {
        Employee_loan existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        existingLoan.setLoanAmount(dto.getLoanAmount());
        existingLoan.setReason(dto.getReason());
        existingLoan.setRemarks(dto.getRemarks());

        Employee_loan saved = loanRepository.save(existingLoan);
        return convertToDTO(saved);
    }

    // DELETE
    public void deleteLoan(Integer id) {
        Employee_loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));
        
        // BUSINESS RULE: Cannot delete a loan with partial repayments recorded
        if (loan.getTotalDeducted() != null && loan.getTotalDeducted().compareTo(java.math.BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Cannot delete a loan that has existing repayments.");
        }
        
        loanRepository.deleteById(id);
    }

    @Autowired
    private com.group_project.wfms_backend.repository.DesignationSalaryRepository designationSalaryRepository;

    // GET MAX LOAN LIMIT (3x monthly salary based on designation)
    public BigDecimal getMaxLoanLimit(Integer employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + employeeId));
        
        if (employee.getDesignation() == null || employee.getDesignation().isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Use the new DesignationSalary table for calculations
        return designationSalaryRepository.findByDesignationNameAndIsActiveTrue(employee.getDesignation())
                .map(ds -> {
                    BigDecimal monthlyEstimate = ds.getBasicSalary();
                    if (ds.getSalaryType() == com.group_project.wfms_backend.model.SalaryRateType.DAILY) {
                        monthlyEstimate = ds.getBasicSalary().multiply(new BigDecimal("26")); // Assume 26 working days
                    }
                    return monthlyEstimate.multiply(new BigDecimal("3"));
                })
                .orElse(BigDecimal.valueOf(45000)); // Default fallback
    }

    @Transactional
    public void recordRepayment(Integer loanId, BigDecimal amount, java.time.LocalDate date, Integer salaryDetailsId) {
        Employee_loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found with ID: " + loanId));

        BigDecimal currentDeducted = loan.getTotalDeducted() != null ? loan.getTotalDeducted() : BigDecimal.ZERO;
        BigDecimal newDeducted = currentDeducted.add(amount);
        loan.setTotalDeducted(newDeducted);

        // Check if fully settled, otherwise reflect partial progress in the status
        if (newDeducted.compareTo(loan.getLoanAmount()) >= 0) {
            loan.setStatus(com.group_project.wfms_backend.model.LoanStatus.SETTLED);

            // Deactivate any active rules for this loan
            // We use the repository directly here for efficiency
            loanDeductionRuleRepository.findActiveRulesByEmployeeId(loan.getEmployee().getId())
                .stream()
                .filter(r -> r.getEmployeeloan().getLoanId().equals(loanId))
                .forEach(r -> {
                    r.setIsActive(false);
                    loanDeductionRuleRepository.save(r);
                });
        } else if (newDeducted.compareTo(BigDecimal.ZERO) > 0) {
            loan.setStatus(com.group_project.wfms_backend.model.LoanStatus.PARTIALLY_PAID);
        }
        loanRepository.save(loan);

        // Record this repayment so callers can avoid double-deducting the same period
        com.group_project.wfms_backend.model.EmployeeLoanDetails detail = new com.group_project.wfms_backend.model.EmployeeLoanDetails();
        detail.setLoan(loan);
        detail.setDate(date != null ? date : java.time.LocalDate.now());
        detail.setAmount(amount);
        detail.setSalaryDetailsId(salaryDetailsId);
        detail.setRemarks("Auto-deducted via payroll confirmation");
        loanDetailsRepository.save(detail);
    }

    public boolean hasRepaymentForPeriod(Integer loanId, Integer month, Integer year) {
        return loanDetailsRepository.existsByLoanIdAndPeriod(loanId, month, year);
    }

    @Autowired
    private com.group_project.wfms_backend.repository.LoanDeductionRuleRepository loanDeductionRuleRepository;

    @Autowired
    private com.group_project.wfms_backend.repository.EmployeeLoanDetailsRepository loanDetailsRepository;

    // MAPPING HELPER
    private EmployeeLoanDTO convertToDTO(Employee_loan loan) {
        EmployeeLoanDTO dto = new EmployeeLoanDTO();
        dto.setLoanId(loan.getLoanId());
        dto.setEmployeeId(loan.getEmployee().getId());
        dto.setEmployeeName(loan.getEmployee().getFullName());
        dto.setLoanAmount(loan.getLoanAmount());
        dto.setIssuedDate(loan.getIssuedDate());
        dto.setReason(loan.getReason());
        dto.setTotalDeducted(loan.getTotalDeducted() != null ? loan.getTotalDeducted() : BigDecimal.ZERO);
        
        // Manual balance calculation for the DTO
        dto.setBalance(dto.getLoanAmount().subtract(dto.getTotalDeducted()));
        
        dto.setStatus(loan.getStatus() != null ? loan.getStatus().toString() : "ACTIVE");
        dto.setRemarks(loan.getRemarks());
        return dto;
    }
}
