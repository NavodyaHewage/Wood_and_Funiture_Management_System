package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.ProfitSummaryDTO;
import com.group_project.wfms_backend.service.ProfitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/profit")
@CrossOrigin
public class ProfitController {
    @Autowired
    private ProfitService profitService;

    @GetMapping("/summary")
    public ResponseEntity<ProfitSummaryDTO> getSummary(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return ResponseEntity.ok(profitService.getProfitSummary(start, end));
    }
}
