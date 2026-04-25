package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.CuttingFeeRequestDTO;
import com.group_project.wfms_backend.dto.auth.CuttingFeeResponseDTO;
import com.group_project.wfms_backend.service.CuttingFeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/cutting-fees")
@RequiredArgsConstructor
public class CuttingFeeController {

    private final CuttingFeeService cuttingFeeService;

    @PostMapping
    public ResponseEntity<CuttingFeeResponseDTO> createCuttingFee(@RequestBody CuttingFeeRequestDTO request) {
        return ResponseEntity.ok(cuttingFeeService.createCuttingFee(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuttingFeeResponseDTO> updateCuttingFee(@PathVariable Integer id, @RequestBody CuttingFeeRequestDTO request) {
        return ResponseEntity.ok(cuttingFeeService.updateCuttingFee(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuttingFeeResponseDTO> getCuttingFeeById(@PathVariable Integer id) {
        return ResponseEntity.ok(cuttingFeeService.getCuttingFeeById(id));
    }

    @GetMapping
    public ResponseEntity<List<CuttingFeeResponseDTO>> getAllCuttingFees() {
        return ResponseEntity.ok(cuttingFeeService.getAllCuttingFees());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCuttingFee(@PathVariable Integer id) {
        cuttingFeeService.deleteCuttingFee(id);
        return ResponseEntity.noContent().build();
    }
}
