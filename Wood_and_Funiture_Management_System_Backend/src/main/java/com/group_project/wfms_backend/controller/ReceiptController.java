package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.ReceiptRequestDTO;
import com.group_project.wfms_backend.dto.auth.ReceiptResponseDTO;
import com.group_project.wfms_backend.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    // ── CREATE ───────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<ReceiptResponseDTO> createReceipt(
            @RequestBody ReceiptRequestDTO dto) {
        ReceiptResponseDTO response = receiptService.createReceipt(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ── GET ALL (PAGINATED & SEARCHABLE) ─────────────────────
    @GetMapping
    public ResponseEntity<java.util.Map<String, Object>> getReceipts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", defaultValue = "date_desc") String sort,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return ResponseEntity.ok(receiptService.getReceipts(search, sort, page, limit));
    }

    // ── GET ALL UNPAGINATED ──────────────────────────────────
    @GetMapping("/all")
    public ResponseEntity<List<ReceiptResponseDTO>> getAllReceipts() {
        return ResponseEntity.ok(receiptService.getAllReceipts());
    }

    // ── GET BY ID ────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ReceiptResponseDTO> getReceiptById(
            @PathVariable Long id) {
        return ResponseEntity.ok(receiptService.getReceiptById(id));
    }

    // ── GET BY CUSTOMER ──────────────────────────────────────
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<ReceiptResponseDTO>> getReceiptsByCustomer(
            @PathVariable Integer customerId) {
        return ResponseEntity.ok(receiptService.getReceiptsByCustomer(customerId));
    }

    // ── UPDATE ───────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<ReceiptResponseDTO> updateReceipt(
            @PathVariable Long id,
            @RequestBody ReceiptRequestDTO dto) {
        return ResponseEntity.ok(receiptService.updateReceipt(id, dto));
    }

    // ── DELETE ───────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceipt(
            @PathVariable Long id) {
        receiptService.deleteReceipt(id);
        return ResponseEntity.noContent().build();
    }

}
