package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.LoanDeductionRuleDTO;
import com.group_project.wfms_backend.service.LoanDeductionRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loan-rules")
@CrossOrigin(origins = "http://localhost:4200")
public class LoanDeductionnRuleController {
    
    @Autowired
    private LoanDeductionRuleService ruleService;

    @GetMapping
    public List<LoanDeductionRuleDTO> getAllRules() {
        return ruleService.getAllRules();
    }

    @PostMapping
    public ResponseEntity<LoanDeductionRuleDTO> createRule(@RequestBody LoanDeductionRuleDTO ruleDto) {
        return ResponseEntity.ok(ruleService.createRule(ruleDto));
    }
}
