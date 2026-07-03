package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.model.ProductCategory;
import com.group_project.wfms_backend.model.RawMaterialItem;
import com.group_project.wfms_backend.repository.ProductCategoryRepository;
import com.group_project.wfms_backend.repository.RawMaterialItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final RawMaterialItemRepository rawMaterialItemRepository;

    @Transactional
    public ProductCategory saveProductCategory(ProductCategory productCategory) {
        resolveRawMaterials(productCategory);
        return productCategoryRepository.save(productCategory);
    }

    public List<ProductCategory> getAllProductCategories() {
        return productCategoryRepository.findAll();
    }

    public Optional<ProductCategory> getProductCategoryById(int id) {
        return productCategoryRepository.findById(id);
    }

    @Transactional
    public void deleteProductCategory(int id) {
        productCategoryRepository.deleteById(id);
    }

    @Transactional
    public ProductCategory updateProductCategory(int id, ProductCategory productCategory) {
        if (productCategoryRepository.existsById(id)) {
            productCategory.setProductCatId(id);
            resolveRawMaterials(productCategory);
            return productCategoryRepository.save(productCategory);
        }
        return null;
    }

    // The frontend only sends raw material IDs (e.g. { rmId: 3 }); re-fetch the managed entities
    // so the many-to-many join table is populated correctly instead of trying to persist
    // detached/incomplete RawMaterialItem stubs.
    private void resolveRawMaterials(ProductCategory productCategory) {
        if (productCategory.getRawMaterials() == null || productCategory.getRawMaterials().isEmpty()) {
            productCategory.setRawMaterials(new java.util.ArrayList<>());
            return;
        }
        List<Integer> ids = productCategory.getRawMaterials().stream()
                .map(RawMaterialItem::getRmId)
                .collect(Collectors.toList());
        productCategory.setRawMaterials(rawMaterialItemRepository.findAllById(ids));
    }
}
