package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.CutProductDTO;
import com.group_project.wfms_backend.dto.CuttingRequestDTO;
import com.group_project.wfms_backend.dto.PendingRawMaterialDTO;
import com.group_project.wfms_backend.model.*;
import com.group_project.wfms_backend.repository.ProductCategoryRepository;
import com.group_project.wfms_backend.repository.ProductStockRepository;
import com.group_project.wfms_backend.repository.SupplyRawMaterialDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RawMaterialCuttingService {

    private final SupplyRawMaterialDetailsRepository detailsRepository;
    private final ProductStockRepository stockRepository;
    private final ProductCategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<PendingRawMaterialDTO> getPendingRawMaterials() {
        return detailsRepository.findByStatus(CuttingStatus.PENDING).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PendingRawMaterialDTO convertToDTO(SupplyRawMaterialDetails details) {
        String invoiceNumber = "N/A";
        if (details.getSupplyRawMaterial() != null) {
            invoiceNumber = details.getSupplyRawMaterial().getInvoiceNumber();
        }
        
        return new PendingRawMaterialDTO(
                details.getId(),
                invoiceNumber,
                details.getLogNumber(),
                details.getLengthFt(),
                details.getGirthFt(),
                details.getTotalQuantityCft(),
                details.getPrice(),
                details.getStatus().name()
        );
    }

    @Transactional
    public void processCutting(CuttingRequestDTO request) {
        // 1. Update Raw Material Status
        List<SupplyRawMaterialDetails> logs = detailsRepository.findAllById(request.getRawMaterialLogIds());
        for (SupplyRawMaterialDetails log : logs) {
            if (log.getStatus() == CuttingStatus.PENDING) {
                log.setStatus(CuttingStatus.CUT);
                log.setCutDay(LocalDate.now());
            }
        }
        detailsRepository.saveAll(logs);

        // 2. Update Product Stock
        for (CutProductDTO cutProduct : request.getCutProducts()) {
            ProductCategory category = categoryRepository.findById(cutProduct.getProductCategoryId())
                    .orElseThrow(() -> new RuntimeException("Product Category not found: " + cutProduct.getProductCategoryId()));

            ProductStock stock = stockRepository.findByProductCategory_ProductCatId(category.getProductCatId())
                    .orElse(new ProductStock());

            if (stock.getStockId() == null) {
                stock.setProductCategory(category);
                stock.setAvailableQuantity(cutProduct.getQuantity());
            } else {
                stock.setAvailableQuantity(stock.getAvailableQuantity().add(cutProduct.getQuantity()));
            }
            stockRepository.save(stock);
        }
    }
}
