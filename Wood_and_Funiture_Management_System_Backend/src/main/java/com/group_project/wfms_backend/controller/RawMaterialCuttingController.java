package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.CuttingRequestDTO;
import com.group_project.wfms_backend.dto.PendingRawMaterialDTO;
import com.group_project.wfms_backend.service.RawMaterialCuttingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/raw-material-cutting")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RawMaterialCuttingController {

    private final RawMaterialCuttingService cuttingService;

    @GetMapping("/pending")
    public ResponseEntity<List<PendingRawMaterialDTO>> getPendingRawMaterials() {
        return ResponseEntity.ok(cuttingService.getPendingRawMaterials());
    }

    @PostMapping("/process")
    public ResponseEntity<String> processCutting(@RequestBody CuttingRequestDTO request) {
        try {
            cuttingService.processCutting(request);
            return ResponseEntity.ok("Cutting process completed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error processing cutting: " + e.getMessage());
        }
    }
}
