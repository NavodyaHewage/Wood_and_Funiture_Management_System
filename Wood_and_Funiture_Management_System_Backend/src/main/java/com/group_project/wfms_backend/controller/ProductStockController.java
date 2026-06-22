package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.ProductStockDTO;
import com.group_project.wfms_backend.service.ProductStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product-stock")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductStockController {

    private final ProductStockService productStockService;

    @GetMapping
    public ResponseEntity<List<ProductStockDTO>> getAllProductStock() {
        return ResponseEntity.ok(productStockService.getAllProductStock());
    }
}
