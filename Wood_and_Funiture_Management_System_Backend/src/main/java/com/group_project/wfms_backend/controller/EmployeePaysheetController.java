package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.PaySheetDTO;
import com.group_project.wfms_backend.service.EmployeePaysheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/paysheet-details")
public class EmployeePaysheetController {
    @Autowired
    private EmployeePaysheetService paysheetService;

    // Paysheets are generated via POST /payroll/confirm (see PayrollController); this endpoint
    // just looks up what has already been generated.

    // සේවකයෙකුගේ සියලුම පේෂීට් විස්තර බැලීමට
    @GetMapping("/employee/{id}")
    public ResponseEntity<List<PaySheetDTO>> getPaysheets(@PathVariable Integer id) {
        return ResponseEntity.ok(paysheetService.getEmployeePaysheets(id));
    }

    // Look up the DAILY paysheet generated for a specific date, for the Payroll Management calendar view
    @GetMapping("/employee/{id}/date/{date}")
    public ResponseEntity<PaySheetDTO> getPaysheetByDate(@PathVariable Integer id,
            @PathVariable @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date) {
        PaySheetDTO dto = paysheetService.getEmployeePaysheetByDate(id, date);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.noContent().build();
    }
}
