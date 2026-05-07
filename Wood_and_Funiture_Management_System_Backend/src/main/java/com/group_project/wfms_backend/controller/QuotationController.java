package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.QuotationRequestDTO;
import com.group_project.wfms_backend.dto.auth.QuotationResponseDTO;
import com.group_project.wfms_backend.model.QuotationStatus;
import com.group_project.wfms_backend.service.QuotationReminderService;
import com.group_project.wfms_backend.service.QuotationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quotations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuotationController {

    private final QuotationService quotationService;
    private final QuotationReminderService quotationReminderService;

    @PostMapping("/check-expiry")
    public ResponseEntity<String> triggerExpiryCheck() {
        return ResponseEntity.ok(quotationReminderService.checkQuotationExpirations());
    }

    @PostMapping
    public ResponseEntity<QuotationResponseDTO> createQuotation(@Valid @RequestBody QuotationRequestDTO requestDTO) {
        return new ResponseEntity<>(quotationService.createQuotation(requestDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<QuotationResponseDTO>> getAllQuotations() {
        return ResponseEntity.ok(quotationService.getAllQuotations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuotationResponseDTO> getQuotationById(@PathVariable Integer id) {
        return ResponseEntity.ok(quotationService.getQuotationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuotationResponseDTO> updateQuotation(@PathVariable Integer id, @Valid @RequestBody QuotationRequestDTO requestDTO) {
        return ResponseEntity.ok(quotationService.updateQuotation(id, requestDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<QuotationResponseDTO> updateQuotationStatus(@PathVariable Integer id, @RequestParam QuotationStatus status) {
        return ResponseEntity.ok(quotationService.updateQuotationStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuotation(@PathVariable Integer id) {
        quotationService.deleteQuotation(id);
        return ResponseEntity.noContent().build();
    }
}