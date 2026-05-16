package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.*;
import com.group_project.wfms_backend.service.SupplyRawMaterialRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/supply-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SupplyRawMaterialRequestController {

    private final SupplyRawMaterialRequestService requestService;

    @PostMapping
    public ResponseEntity<SupplyRequestResponseDTO> createRequest(@RequestBody SupplyRequestDTO dto) {
        return ResponseEntity.ok(requestService.createRequest(dto));
    }

    @GetMapping
    public ResponseEntity<List<SupplyRequestResponseDTO>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyRequestResponseDTO> getRequestById(@PathVariable Integer id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<SupplyRequestResponseDTO> processSupplierApproval(
            @PathVariable Integer id,
            @RequestBody List<SupplyRequestDetailDTO> details) {
        return ResponseEntity.ok(requestService.processSupplierApproval(id, details));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<SupplyRequestResponseDTO> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        return ResponseEntity.ok(requestService.updateStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(@PathVariable Integer id) {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/convert")
    public ResponseEntity<SupplyRawMaterialResponseDTO> convertToSupplyOrder(
            @PathVariable Integer id,
            @RequestBody SupplyRawMaterialRequestDTO supplyOrderDto) {
        return ResponseEntity.ok(requestService.convertToSupplyOrder(id, supplyOrderDto));
    }
}
