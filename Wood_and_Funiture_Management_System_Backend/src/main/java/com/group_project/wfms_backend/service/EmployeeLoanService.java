package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.EmployeeLoanDTO;
import com.group_project.wfms_backend.model.Employee_loan;
import com.group_project.wfms_backend.repository.EmployeeRepository;
import com.group_project.wfms_backend.repository.Employeeloanrepository;
import com.group_project.wfms_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    // READ ALL
    public List<EmployeeLoanDTO> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // READ BY ID
    public EmployeeLoanDTO getLoanById(Integer id) {
        Employee_loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found with id: " + id));
        return convertToDTO(loan);
    }

    // CREATE
    @Transactional
    public EmployeeLoanDTO createLoan(EmployeeLoanDTO dto) {
        Employee_loan loan = new Employee_loan();

        // Map simple fields
        loan.setLoanId(dto.getLoanId());
        loan.setLoanAmount(dto.getLoanAmount());
        loan.setIssuedDate(dto.getIssuedDate());
        loan.setReason(dto.getReason());
        loan.setRemarks(dto.getRemarks());

        // Resolve Relationships
        loan.setEmployee(employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found")));

        if (dto.getCreatedById() != null) {
            loan.setCreatedBy(userRepository.findById(dto.getCreatedById()).orElse(null));
        }

        Employee_loan savedLoan = loanRepository.save(loan);
        return convertToDTO(savedLoan);
    }

    // UPDATE
    @Transactional
    public EmployeeLoanDTO updateLoan(Integer id, EmployeeLoanDTO dto) {
        Employee_loan existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        existingLoan.setLoanAmount(dto.getLoanAmount());
        existingLoan.setReason(dto.getReason());
        existingLoan.setRemarks(dto.getRemarks());
        // Status updates (e.g., from ACTIVE to CLOSED)
        if(dto.getStatus() != null) {
            // existingLoan.setStatus(LoanStatus.valueOf(dto.getStatus()));
        }

        return convertToDTO(loanRepository.save(existingLoan));
    }

    // DELETE
    public void deleteLoan(Integer id) {
        loanRepository.deleteById(id);
    }

    // MAPPING HELPER
    private EmployeeLoanDTO convertToDTO(Employee_loan loan) {
        EmployeeLoanDTO dto = new EmployeeLoanDTO();
        dto.setLoanId(loan.getLoanId());
        dto.setEmployeeId(loan.getEmployee().getId());
        dto.setLoanAmount(loan.getLoanAmount());
        dto.setIssuedDate(loan.getIssuedDate());
        dto.setReason(loan.getReason());
        dto.setTotalDeducted(loan.getTotalDeducted());
        dto.setBalance(loan.getBalance()); // Calculated by DB
        dto.setStatus(loan.getStatus().toString());
        dto.setRemarks(loan.getRemarks());
        return dto;
    }
}
