package com.group_project.wfms_backend.controller;


import com.group_project.wfms_backend.dto.auth.ReceiptDTO;
import com.group_project.wfms_backend.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/receipts")
@CrossOrigin(origins = "http://localhost:4200")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @PostMapping("/create")
    public ResponseEntity<String> createReceipt(@RequestBody ReceiptDTO dto) {
        try {
            receiptService.createFullReceipt(dto);
            return ResponseEntity.ok("Receipt, Details and Income record created successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
