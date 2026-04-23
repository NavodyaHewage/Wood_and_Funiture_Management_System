package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.EmployeeSalaryDTO;
import com.group_project.wfms_backend.service.PayrollRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payroll-records")
@CrossOrigin(origins = "http://localhost:4200") // Angular port එකට අවසර දීම
public class PayrollRecordController {

    @Autowired
    private PayrollRecordService recordService;

    // වැටුප් වාර්තාවක් සෑදීමට
    @PostMapping
    public ResponseEntity<EmployeeSalaryDTO> create(@RequestBody EmployeeSalaryDTO dto) {
        return ResponseEntity.ok(recordService.createSalaryRecord(dto));
    }

    // සියලුම වාර්තා බැලීමට
    @GetMapping
    public ResponseEntity<List<EmployeeSalaryDTO>> getAll() {
        return ResponseEntity.ok(recordService.getAllSalaryRecords());
    }

    // ID එක අනුව සෙවීමට
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeSalaryDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(recordService.getSalaryById(id));
    }
}
