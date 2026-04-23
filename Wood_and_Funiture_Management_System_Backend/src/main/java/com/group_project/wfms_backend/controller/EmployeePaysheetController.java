package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.PaySheetDTO;
import com.group_project.wfms_backend.service.EmployeePaysheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/paysheet-details")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeePaysheetController {
    @Autowired
    private EmployeePaysheetService paysheetService;

    // පේෂීට් එකක් Generate කර Database එකේ Save කිරීමට
    @PostMapping("/generate")
    public ResponseEntity<String> generatePaysheet(
            @RequestParam Integer employeeId,
            @RequestParam Integer month,
            @RequestParam Integer year) {

        paysheetService.generateAndSavePaysheet(employeeId, month, year);
        return ResponseEntity.ok("Paysheet generated and saved successfully!");
    }

    // සේවකයෙකුගේ සියලුම පේෂීට් විස්තර බැලීමට
    @GetMapping("/employee/{id}")
    public ResponseEntity<List<PaySheetDTO>> getPaysheets(@PathVariable Integer id) {
        return ResponseEntity.ok(paysheetService.getEmployeePaysheets(id));
    }
}
