package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.LoanDeductionRuleDTO;
import com.group_project.wfms_backend.model.Employee_loan;
import com.group_project.wfms_backend.model.Loan_Deduction_Rule;
import com.group_project.wfms_backend.repository.Employeeloanrepository;
import com.group_project.wfms_backend.repository.LoanDeductionRuleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanDeductionRuleService {
    @Autowired
    private LoanDeductionRuleRepository ruleRepository;

    @Autowired
    private Employeeloanrepository loanRepository;

    @Autowired
    private com.group_project.wfms_backend.repository.DesignationSalaryRepository designationSalaryRepository;

    // READ ALL
    public List<LoanDeductionRuleDTO> getAllRules() {
        return ruleRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // CREATE RULE
    @Transactional
    public LoanDeductionRuleDTO createRule(LoanDeductionRuleDTO dto) {
        // Link to the parent Loan
        Employee_loan parentLoan = loanRepository.findById(dto.getLoanId())
                .orElseThrow(() -> new EntityNotFoundException("Loan ID not found with ID: " + dto.getLoanId()));

        // BUSINESS RULE: Cannot create rule for a SETTLED loan
        if (com.group_project.wfms_backend.model.LoanStatus.SETTLED.equals(parentLoan.getStatus())) {
            throw new RuntimeException("Cannot create deduction rules for a fully settled loan.");
        }

        // VALIDATION: Period Chronology
        if (dto.getEndYear() != null && (dto.getEndYear() < dto.getStartYear() || 
           (dto.getEndYear().equals(dto.getStartYear()) && dto.getEndMonth() != null && dto.getEndMonth() < dto.getStartMonth()))) {
            throw new RuntimeException("End period must be after start period.");
        }

        // Resolve the installment type from incoming rule remarks tag first, fallback to designation salary type
        com.group_project.wfms_backend.model.SalaryRateType salaryType = null;
        if (dto.getRemarks() != null) {
            if (dto.getRemarks().startsWith("[DAILY]")) {
                salaryType = com.group_project.wfms_backend.model.SalaryRateType.DAILY;
            } else if (dto.getRemarks().startsWith("[MONTHLY]")) {
                salaryType = com.group_project.wfms_backend.model.SalaryRateType.MONTHLY;
            }
        }

        if (salaryType == null) {
            String designation = parentLoan.getEmployee().getDesignation();
            salaryType = com.group_project.wfms_backend.model.SalaryRateType.MONTHLY;
            if (designation != null && !designation.isEmpty()) {
                salaryType = designationSalaryRepository.findByDesignationNameAndIsActiveTrue(designation)
                        .map(com.group_project.wfms_backend.model.DesignationSalary::getSalaryType)
                        .orElse(com.group_project.wfms_backend.model.SalaryRateType.MONTHLY);
            }
        }

        String periodType = (salaryType == com.group_project.wfms_backend.model.SalaryRateType.DAILY) ? "Daily" : "Monthly";

        // VALIDATION: Deduction vs Outstanding Balance
        java.math.BigDecimal balance = parentLoan.getLoanAmount().subtract(parentLoan.getTotalDeducted() != null ? parentLoan.getTotalDeducted() : java.math.BigDecimal.ZERO);
        if (dto.getDeductionAmount().compareTo(balance) > 0) {
            throw new RuntimeException(periodType + " deduction (Rs. " + dto.getDeductionAmount() + ") cannot exceed outstanding balance (Rs. " + balance + ").");
        }

        Loan_Deduction_Rule rule = new Loan_Deduction_Rule();
        rule.setEmployeeloan(parentLoan);
        rule.setDeductionAmount(dto.getDeductionAmount());
        rule.setStartMonth(dto.getStartMonth());
        rule.setStartYear(dto.getStartYear());
        rule.setEndMonth(dto.getEndMonth());
        rule.setEndYear(dto.getEndYear());
        rule.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        rule.setRemarks(dto.getRemarks());

        return convertToDTO(ruleRepository.save(rule));
    }

    // TOGGLE STATUS (Helper for CRUD)
    @Transactional
    public void toggleRuleStatus(Integer id, Boolean status) {
        Loan_Deduction_Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rule not found"));
        rule.setIsActive(status);
        ruleRepository.save(rule);
    }

    // DELETE
    public void deleteRule(Integer id) {
        ruleRepository.deleteById(id);
    }

    // MAPPING HELPER
    private LoanDeductionRuleDTO convertToDTO(Loan_Deduction_Rule rule) {
        LoanDeductionRuleDTO dto = new LoanDeductionRuleDTO();
        dto.setRuleId(rule.getRuleId());
        dto.setLoanId(rule.getEmployeeloan().getLoanId());
        dto.setDeductionAmount(rule.getDeductionAmount());
        dto.setStartMonth(rule.getStartMonth());
        dto.setStartYear(rule.getStartYear());
        dto.setEndMonth(rule.getEndMonth());
        dto.setEndYear(rule.getEndYear());
        dto.setIsActive(rule.getIsActive());
        dto.setRemarks(rule.getRemarks());
        return dto;
    }
}
