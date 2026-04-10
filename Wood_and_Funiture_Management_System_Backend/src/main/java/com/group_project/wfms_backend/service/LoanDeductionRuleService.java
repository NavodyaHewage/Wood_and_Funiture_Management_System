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
        Loan_Deduction_Rule rule = new Loan_Deduction_Rule();

        // Link to the parent Loan
        Employee_loan parentLoan = loanRepository.findById(dto.getLoanId())
                .orElseThrow(() -> new EntityNotFoundException("Loan ID not found with ID: " + dto.getLoanId()));

        rule.setEmployeeloan(parentLoan);
        rule.setDeductionAmount(dto.getDeductionAmount());
        rule.setStartMonth(dto.getStartMonth());
        rule.setStartYear(dto.getStartYear());
        rule.setEndMonth(dto.getEndMonth());
        rule.setEndYear(dto.getEndYear());
        rule.setIsActive(dto.getIsActive());
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
