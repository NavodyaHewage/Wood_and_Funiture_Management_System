package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.IncomeAccountDTO;
import com.group_project.wfms_backend.service.IncomeAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/income-account")
@CrossOrigin(origins = "http://localhost:4200")
public class IncomeAccountController {
    @Autowired
    private IncomeAccountService incomeService;

    @GetMapping
    public ResponseEntity<List<IncomeAccountDTO>> getAll() {
        return ResponseEntity.ok(incomeService.getAllIncomeRecords());
    }

    @PostMapping("/manual")
    public ResponseEntity<IncomeAccountDTO> createManual(@RequestBody IncomeAccountDTO dto) {
        return ResponseEntity.ok(incomeService.createManualIncome(dto));
    }
}
