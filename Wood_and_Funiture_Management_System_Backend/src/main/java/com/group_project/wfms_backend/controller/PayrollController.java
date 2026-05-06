package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.PayrollRequestDTO;
import com.group_project.wfms_backend.dto.auth.PayrollResponseDTO;
import com.group_project.wfms_backend.service.EmployeePaysheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payroll")
public class PayrollController {

    @Autowired
    private EmployeePaysheetService paysheetService;

    @PostMapping("/calculate")
    public ResponseEntity<PayrollResponseDTO> calculatePayroll(@RequestBody PayrollRequestDTO request) {
        return ResponseEntity.ok(paysheetService.calculatePayroll(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<String> confirmPayroll(@RequestBody PayrollRequestDTO request, @RequestParam Integer userId) {
        paysheetService.confirmPayroll(request, userId);
        return ResponseEntity.ok("Payroll confirmed and expense record created");
    }
}
