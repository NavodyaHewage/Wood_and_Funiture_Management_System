package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.SupplyRawMaterialRequestDTO;
import com.group_project.wfms_backend.dto.auth.SupplyRawMaterialResponseDTO;
import com.group_project.wfms_backend.security.UserDetailsImpl;
import com.group_project.wfms_backend.service.SupplyRawMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/supply-raw-materials")
@RequiredArgsConstructor
public class SupplyRawMaterialController {

    private final SupplyRawMaterialService supplyRawMaterialService;

    @PostMapping
    public ResponseEntity<SupplyRawMaterialResponseDTO> createSupplyRawMaterial(@RequestBody SupplyRawMaterialRequestDTO request) {
        return ResponseEntity.ok(supplyRawMaterialService.createSupplyRawMaterial(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplyRawMaterialResponseDTO> updateSupplyRawMaterial(@PathVariable Integer id, @RequestBody SupplyRawMaterialRequestDTO request) {
        return ResponseEntity.ok(supplyRawMaterialService.updateSupplyRawMaterial(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplyRawMaterialResponseDTO> getSupplyRawMaterialById(@PathVariable Integer id) {
        return ResponseEntity.ok(supplyRawMaterialService.getSupplyRawMaterialById(id));
    }

    @GetMapping
    public ResponseEntity<List<SupplyRawMaterialResponseDTO>> getAllSupplyRawMaterials() {
        return ResponseEntity.ok(supplyRawMaterialService.getAllSupplyRawMaterials());
    }

    @GetMapping("/my-supplies")
    public ResponseEntity<List<SupplyRawMaterialResponseDTO>> getMySupplyRawMaterials(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(required = false) String email) {
        return ResponseEntity.ok(supplyRawMaterialService.getSupplyRawMaterialsForLoggedSupplier(userDetails, email));
    }

    @GetMapping("/grn-by-invoice/{invoiceNumber}")
    public ResponseEntity<Integer> getGrnIdByInvoiceNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(supplyRawMaterialService.getGrnIdByInvoiceNumber(invoiceNumber));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplyRawMaterial(@PathVariable Integer id) {
        supplyRawMaterialService.deleteSupplyRawMaterial(id);
        return ResponseEntity.noContent().build();
    }
}
