package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.ProductStockDTO;
import com.group_project.wfms_backend.repository.ProductStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductStockService {

    private final ProductStockRepository productStockRepository;

    @Transactional(readOnly = true)
    public List<ProductStockDTO> getAllProductStock() {
        return productStockRepository.findAll().stream()
                .map(ProductStockDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
