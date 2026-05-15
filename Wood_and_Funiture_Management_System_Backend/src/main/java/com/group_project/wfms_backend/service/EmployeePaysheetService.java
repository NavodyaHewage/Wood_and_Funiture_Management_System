package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.PaySheetDTO;
import com.group_project.wfms_backend.dto.auth.PayrollRequestDTO;
import com.group_project.wfms_backend.dto.auth.PayrollResponseDTO;
import com.group_project.wfms_backend.model.*;
import com.group_project.wfms_backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeePaysheetService {
    @Autowired private EmployeePaysheetRepository paysheetRepository;
    @Autowired private EmployeeSalaryRepository salaryRepository;
    @Autowired private EmployeeAttendanceRepository attendanceRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private DesignationSalaryRepository designationSalaryRepository;
    @Autowired private LoanDeductionRuleRepository loanDeductionRuleRepository;
    @Autowired private EmployeeSalaryPaymentRepository salaryPaymentRepository;
    @Autowired private ExpenseAccountRepository expenseAccountRepository;
    @Autowired private ExpenseTypeRepository expenseTypeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EmployeeLoanService loanService;

    @Transactional
    public PayrollResponseDTO calculatePayroll(PayrollRequestDTO request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        if (!Boolean.TRUE.equals(employee.getIsActive())) {
            throw new RuntimeException("Cannot calculate payroll for inactive employee");
        }

        // Check if finalized
        if (salaryRepository.existsByEmployeeIdAndMonthAndYearAndStatus(request.getEmployeeId(), request.getMonth(), request.getYear(), Salary_details_Status.PAID)) {
            throw new RuntimeException("Payroll already finalized for this period");
        }

        PayrollResponseDTO response = new PayrollResponseDTO();
        response.setEmployeeName(employee.getFullName());
        response.setDesignation(employee.getDesignation());
        response.setAttendanceWarnings(new ArrayList<>());

        // 1. Basic Salary from Designation Meta-Table
        DesignationSalary ds = designationSalaryRepository.findByDesignationNameAndIsActiveTrue(employee.getDesignation())
                .orElseThrow(() -> new RuntimeException("Salary Mapping Error: No salary record found for designation '" + employee.getDesignation() + "'. Please add it in the Designation Salary section."));

        BigDecimal baseSalary;
        // Divergent logic: Daily workers are paid per attended day, whereas monthly workers receive a fixed base regardless of exact days, unless unpaid leave is applied.
        if ("DAILY".equalsIgnoreCase(request.getPaymentType())) {
            // Calculate for TODAY only
            baseSalary = ds.getBasicSalary(); // Assuming basicSalary in meta-table is the Daily Rate for Daily types
            if (ds.getSalaryType() == SalaryRateType.MONTHLY) {
                baseSalary = ds.getBasicSalary().divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
            }
        } else if (ds.getSalaryType() == SalaryRateType.DAILY) {
            long presentDays = attendanceRepository.countByEmployeeIdAndMonthAndYearAndStatus(employee.getId(), request.getMonth(), request.getYear(), AttendanceStatus.PRESENT);
            long halfDays = attendanceRepository.countByEmployeeIdAndMonthAndYearAndStatus(employee.getId(), request.getMonth(), request.getYear(), AttendanceStatus.HALF_DAY);
            
            // Working days: Present + (Half Days * 0.5)
            BigDecimal totalWorkingDays = BigDecimal.valueOf(presentDays).add(BigDecimal.valueOf(halfDays).multiply(BigDecimal.valueOf(0.5)));
            baseSalary = ds.getBasicSalary().multiply(totalWorkingDays);
        } else {
            baseSalary = ds.getBasicSalary();
        }
        response.setBaseSalary(baseSalary.setScale(2, RoundingMode.HALF_UP));

        // 2. Overtime Calculation
        double totalOtHours = 0;
        if (!"DAILY".equalsIgnoreCase(request.getPaymentType())) {
            totalOtHours = attendanceRepository.findByMonthAndYear(request.getMonth(), request.getYear()).stream()
                    .filter(a -> a.getEmployee().getId().equals(employee.getId()))
                    .mapToDouble(this::calculateDailyOT)
                    .sum();
        } else {
            // Check OT for today only
            totalOtHours = attendanceRepository.findByEmployeeIdAndDate(employee.getId(), java.time.LocalDate.now())
                    .map(this::calculateDailyOT)
                    .orElse(0.0);
        }
        response.setOvertimeHours(totalOtHours);

        // OT Rate Calculation (Assumed 1.5x of hourly rate)
        // Overtime is calculated at 1.5x the standard hourly rate based on the employee's designation.
        BigDecimal hourlyRate = (ds.getSalaryType() == SalaryRateType.DAILY) 
                ? ds.getBasicSalary().divide(BigDecimal.valueOf(8), 2, RoundingMode.HALF_UP)
                : ds.getBasicSalary().divide(BigDecimal.valueOf(200), 2, RoundingMode.HALF_UP); // 200h/month approx
        
        BigDecimal otAmount = hourlyRate.multiply(BigDecimal.valueOf(1.5)).multiply(BigDecimal.valueOf(totalOtHours));
        response.setOvertimeAmount(otAmount.setScale(2, RoundingMode.HALF_UP));

        // 3. Loan Deduction
        BigDecimal totalLoanDeduction = BigDecimal.ZERO;
        if (Boolean.TRUE.equals(request.getIsLoanDeductionEnabled())) {
            List<Loan_Deduction_Rule> activeRules = loanDeductionRuleRepository.findActiveRulesByEmployeeId(employee.getId());
            totalLoanDeduction = activeRules.stream()
                    .map(Loan_Deduction_Rule::getDeductionAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (request.getLoanDeductionOverride() != null && request.getLoanDeductionOverride().compareTo(totalLoanDeduction) > 0) {
                totalLoanDeduction = request.getLoanDeductionOverride();
            }
        }
        response.setLoanDeduction(totalLoanDeduction);

        // 4. Other Deductions
        BigDecimal other = request.getOtherDeduction() != null ? request.getOtherDeduction() : BigDecimal.ZERO;
        response.setOtherDeduction(other);

        // 5. Subtract Previously Paid Amounts (Daily Issues)
        BigDecimal paidAmount = BigDecimal.ZERO;
        if (!"DAILY".equalsIgnoreCase(request.getPaymentType())) {
            paidAmount = salaryRepository.findByEmployeeIdAndMonthAndYear(employee.getId(), request.getMonth(), request.getYear())
                    .map(details -> salaryPaymentRepository.sumAmountBySalaryDetailsId(details.getSalaryDetailsId()))
                    .orElse(BigDecimal.ZERO);
        }
        response.setPreviouslyPaidAmount(paidAmount);

        // 6. Final Net Salary
        BigDecimal netSalary = baseSalary.add(otAmount).subtract(totalLoanDeduction).subtract(other).subtract(paidAmount);
        
        if (netSalary.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Net salary cannot be negative (Rs. " + netSalary + "). Please adjust deductions.");
        }
        response.setNetSalary(netSalary.setScale(2, RoundingMode.HALF_UP));

        return response;
    }

    @Transactional
    public void confirmPayroll(PayrollRequestDTO request, Integer userId) {
        // This method is fully transactional. It ensures that Paysheets, Expense ledgers, and Loan balances are all updated atomically to prevent data inconsistency.
        PayrollResponseDTO response = calculatePayroll(request);
        Employee employee = employeeRepository.findById(request.getEmployeeId()).orElseThrow();
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Logged in user not found"));

        // 1. Create/Update Salary Details (Main Record)
        EmployeeSalaryDetails salary = salaryRepository.findByEmployeeIdAndMonthAndYear(request.getEmployeeId(), request.getMonth(), request.getYear())
                .orElse(new EmployeeSalaryDetails());
        
        salary.setEmployee(employee);
        salary.setMonth(request.getMonth());
        salary.setYear(request.getYear());
        salary.setIsActive(true);

        if ("DAILY".equalsIgnoreCase(request.getPaymentType())) {
            // Increment Paid Amount and potentially Total Amount
            salary.setTotalAmount(salary.getTotalAmount().add(response.getNetSalary()));
            salary.setPaidAmount(salary.getPaidAmount().add(response.getNetSalary()));
            salary.setStatus(salary.getPaidAmount().compareTo(salary.getTotalAmount()) >= 0 ? Salary_details_Status.PAID : Salary_details_Status.PARTIALLY_PAID);
        } else {
            // Full Monthly Calculation
            salary.setTotalAmount(response.getNetSalary().add(response.getPreviouslyPaidAmount()));
            salary.setStatus(Salary_details_Status.PENDING);
        }
        
        EmployeeSalaryDetails savedSalary = salaryRepository.save(salary);

        // 2. Create Salary Payment Record (For history)
        EmployeeSalaryPayment payment = new EmployeeSalaryPayment();
        payment.setSalaryDetails(savedSalary);
        payment.setDate(java.time.LocalDate.now());
        payment.setAmount(response.getNetSalary());
        payment.setPaymentMethod(PaymentMethod.CASH);
        payment.setPaidBy(currentUser);
        payment.setRemarks("Processed via " + request.getPaymentType() + " automation");
        salaryPaymentRepository.save(payment);

        // 3. Create Paysheet Snapshot (Optional: only for Monthly or as a history log)
        EmployeePaysheet paysheet = new EmployeePaysheet(); // New entry for every payment or update existing? Let's update monthly.
        paysheet = paysheetRepository.findByEmployeeIdAndMonthAndYear(request.getEmployeeId(), request.getMonth(), request.getYear())
                .orElse(new EmployeePaysheet());
        
        paysheet.setEmployee(employee);
        paysheet.setMonth(request.getMonth());
        paysheet.setYear(request.getYear());
        paysheet.setBaseSalary(response.getBaseSalary());
        paysheet.setOvertimeAmount(response.getOvertimeAmount());
        paysheet.setLoanDeduction(response.getLoanDeduction());
        paysheet.setOtherDeduction(response.getOtherDeduction());
        paysheet.setTotalEarnings(response.getBaseSalary().add(response.getOvertimeAmount()));
        paysheet.setNetSalary(response.getNetSalary());
        paysheetRepository.save(paysheet);

        // 3. Auto-create Expense Record
        ExpenseType salaryType = expenseTypeRepository.findAll().stream()
                .filter(t -> t.getTypeName().equalsIgnoreCase("Salary"))
                .findFirst()
                .orElseGet(() -> {
                    ExpenseType t = new ExpenseType();
                    t.setTypeName("Salary");
                    t.setDescription("Employee Salary Expenses");
                    return expenseTypeRepository.save(t);
                });

        Expenseaccount expense = new Expenseaccount();
        expense.setDate(java.time.LocalDate.now());
        expense.setAmount(response.getNetSalary());
        expense.setDescription("Salary for " + employee.getFullName() + " - " + request.getMonth() + "/" + request.getYear());
        expense.setPaidTo(employee.getFullName());
        expense.setExpenseType(salaryType);
        expense.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Logged in user not found")));
        expenseAccountRepository.save(expense);

        // 4. Update Loan Outstanding Balances and Deactivate Rules via Reusable Service
        List<Loan_Deduction_Rule> activeRules = loanDeductionRuleRepository.findActiveRulesByEmployeeId(employee.getId());
        for (Loan_Deduction_Rule rule : activeRules) {
            loanService.recordRepayment(rule.getEmployeeloan().getLoanId(), rule.getDeductionAmount());
        }
    }

    private double calculateDailyOT(EmployeeAttendance a) {
        if (a.getCheckOut() != null && a.getCheckOut().isAfter(LocalTime.of(17, 0))) {
            long seconds = java.time.Duration.between(LocalTime.of(17, 0), a.getCheckOut()).getSeconds();
            return seconds / 3600.0;
        }
        return 0.0;
    }

    @Transactional
    public void generateAndSavePaysheet(Integer employeeId, Integer month, Integer year) {
        // Existing method logic (can be updated to use calculatePayroll logic)
    }

    // සේවකයෙකුගේ පේෂීට් එකක් preview බැලීම සඳහා
    public List<PaySheetDTO> getEmployeePaysheets(Integer employeeId) {
        return paysheetRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PaySheetDTO convertToDTO(EmployeePaysheet entity) {
        PaySheetDTO dto = new PaySheetDTO();
        dto.setEmployeeName(entity.getEmployee().getFullName());
        dto.setDesignation(entity.getEmployee().getDesignation());
        dto.setMonth(entity.getMonth());
        dto.setYear(entity.getYear());
        dto.setPresentDays(entity.getPresentDays());
        dto.setTotalEarnings(entity.getTotalEarnings());
        dto.setNetSalary(entity.getNetSalary());
        return dto;
    }
}
