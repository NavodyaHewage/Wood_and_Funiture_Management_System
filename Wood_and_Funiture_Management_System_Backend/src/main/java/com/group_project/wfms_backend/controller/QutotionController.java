package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.QuotationRequestDTO;
import com.group_project.wfms_backend.dto.auth.QuotationResponseDTO;
import com.group_project.wfms_backend.model.QuotationStatus;
import com.group_project.wfms_backend.service.QuotationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/quotations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QutotionController {

    private final QuotationService quotationService;

    @PostMapping
    public ResponseEntity<QuotationResponseDTO> createQuotation(
            @Valid @RequestBody QuotationRequestDTO requestDTO) {
        QuotationResponseDTO created = quotationService.createQuotation(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @GetMapping
    public ResponseEntity<List<QuotationResponseDTO>> getAllQuotations() {
        return ResponseEntity.ok(quotationService.getAllQuotations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuotationResponseDTO> getQuotationById(@PathVariable Integer id) {
        return ResponseEntity.ok(quotationService.getQuotationById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<QuotationResponseDTO>> getQuotationsByCustomer(
            @PathVariable Integer customerId) {
        return ResponseEntity.ok(quotationService.getQuotationsByCustomer(customerId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<QuotationResponseDTO>> getQuotationsByStatus(
            @PathVariable QuotationStatus status) {
        return ResponseEntity.ok(quotationService.getQuotationsByStatus(status));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<QuotationResponseDTO>> getQuotationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(quotationService.getQuotationsByDateRange(startDate, endDate));
    }


    @PutMapping("/{id}")
    public ResponseEntity<QuotationResponseDTO> updateQuotation(
            @PathVariable Integer id,
            @Valid @RequestBody QuotationRequestDTO requestDTO) {
        return ResponseEntity.ok(quotationService.updateQuotation(id, requestDTO));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<QuotationResponseDTO> updateQuotationStatus(
            @PathVariable Integer id,
            @RequestParam QuotationStatus status) {
        return ResponseEntity.ok(quotationService.updateQuotationStatus(id, status));
    }



    @PostMapping("/{id}/convert")
    public ResponseEntity<QuotationResponseDTO> convertToOrder(@PathVariable Integer id) {
        return ResponseEntity.ok(quotationService.convertQuotationToOrder(id));
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuotation(@PathVariable Integer id) {
        quotationService.deleteQuotation(id);
        return ResponseEntity.noContent().build();
    }
}