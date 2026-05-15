package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.SupplyRawMaterialRequestDTO;
import com.group_project.wfms_backend.dto.auth.SupplyRawMaterialResponseDTO;
import com.group_project.wfms_backend.service.SupplyRawMaterialService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/supplier/{email}")
    public ResponseEntity<List<SupplyRawMaterialResponseDTO>> getSuppliesBySupplierEmail(@PathVariable String email) {
        return ResponseEntity.ok(supplyRawMaterialService.getSuppliesBySupplierEmail(email));
    }

    @GetMapping("/latest-log-number")
    public ResponseEntity<Integer> getLatestLogNumber() {
        return ResponseEntity.ok(supplyRawMaterialService.getLatestLogNumber());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplyRawMaterial(@PathVariable Integer id) {
        supplyRawMaterialService.deleteSupplyRawMaterial(id);
        return ResponseEntity.noContent().build();
    }
}
