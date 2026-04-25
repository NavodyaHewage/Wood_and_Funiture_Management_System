package com.group_project.wfms_backend.controller;


import com.group_project.wfms_backend.dto.auth.EmployeeLoanDTO;
//import com.group_project.wfms_backend.model.Employee_loan;
import com.group_project.wfms_backend.service.EmployeeLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "http://localhost:4200")

public class EmployeeLoanController {
    @Autowired
    private EmployeeLoanService loanService;

    @GetMapping
    public List<EmployeeLoanDTO> getAll() {
        return loanService.getAllLoans();
    }

    @PostMapping
    public ResponseEntity<EmployeeLoanDTO> create(@RequestBody EmployeeLoanDTO loanDto) {
        return ResponseEntity.ok(loanService.createLoan(loanDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        loanService.deleteLoan(id);
        return ResponseEntity.noContent().build();
    }
}
