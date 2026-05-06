package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.RawMaterialItemResponseDTO;
import com.group_project.wfms_backend.service.RawMaterialItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/raw-material-items")
@RequiredArgsConstructor
public class RawMaterialItemController {

    private final RawMaterialItemService rawMaterialItemService;

    @GetMapping
    public ResponseEntity<List<RawMaterialItemResponseDTO>> getAllRawMaterialItems() {
        return ResponseEntity.ok(rawMaterialItemService.getAllRawMaterialItems());
    }
}
