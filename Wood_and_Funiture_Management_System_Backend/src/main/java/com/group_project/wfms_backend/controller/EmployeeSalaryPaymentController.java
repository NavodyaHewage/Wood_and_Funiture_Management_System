package com.group_project.wfms_backend.controller;


import com.group_project.wfms_backend.dto.auth.EmployeeSalaryPaymentDTO;
import com.group_project.wfms_backend.service.EmployeeSalaryPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/salary-payments")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeSalaryPaymentController {

    @Autowired
    private EmployeeSalaryPaymentService paymentService;

    // ගෙවීමක් සිදුකිරීමට (Transaction)
    @PostMapping
    public ResponseEntity<EmployeeSalaryPaymentDTO> processPayment(@RequestBody EmployeeSalaryPaymentDTO dto) {
        return ResponseEntity.ok(paymentService.makePayment(dto));
    }

    // අදාළ වැටුප් වාර්තාවේ ID එක අනුව ගෙවීම් ඉතිහාසය බැලීමට
    @GetMapping("/history/{salaryDetailsId}")
    public ResponseEntity<List<EmployeeSalaryPaymentDTO>> getHistory(@PathVariable Integer salaryDetailsId) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(salaryDetailsId));
    }

}
