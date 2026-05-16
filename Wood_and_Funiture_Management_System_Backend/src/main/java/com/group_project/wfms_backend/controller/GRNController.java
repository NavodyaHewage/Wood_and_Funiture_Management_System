package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.GrnResponseDTO;
import com.group_project.wfms_backend.service.GRNService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/grn")
@RequiredArgsConstructor
@CrossOrigin
public class GRNController {

    private final GRNService grnService;

    @GetMapping("/{id}")
    public ResponseEntity<GrnResponseDTO> getGrnById(@PathVariable Integer id) {
        return ResponseEntity.ok(grnService.getGrnById(id));
    }

    @GetMapping
    public ResponseEntity<List<GrnResponseDTO>> getAllGrns() {
        return ResponseEntity.ok(grnService.getAllGrns());
    }
}
