package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.finance.AccountsDashboardDTO;
import com.group_project.wfms_backend.service.AccountsDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/accounts-dashboard")
@CrossOrigin
public class AccountsDashboardController {

    @Autowired
    private AccountsDashboardService accountsDashboardService;

    @GetMapping("/summary")
    public ResponseEntity<AccountsDashboardDTO> getDashboardSummary(
            @RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        
        // Default to current month if not provided
        if (start == null) {
            start = LocalDate.now().withDayOfMonth(1);
        }
        if (end == null) {
            end = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        }

        return ResponseEntity.ok(accountsDashboardService.getDashboardData(start, end));
    }
}
