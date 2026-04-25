package com.group_project.wfms_backend.controller;


import com.group_project.wfms_backend.model.Loan_Deduction_Rule;
import com.group_project.wfms_backend.repository.LoanDeductionRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan-rules")
@CrossOrigin(origins = "http://localhost:4200")

public class LoanDeductionnRuleController {
    @Autowired
    private LoanDeductionRuleRepository ruleRepository;

    @GetMapping
    public List<Loan_Deduction_Rule> getAllRules() {
        return ruleRepository.findAll();
    }

    @PostMapping
    public Loan_Deduction_Rule createRule(@RequestBody Loan_Deduction_Rule rule) {
        return ruleRepository.save(rule);
    }
}
