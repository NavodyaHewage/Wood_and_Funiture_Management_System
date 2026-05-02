package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.model.ProductCategory;
import com.group_project.wfms_backend.repository.ProductCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;

    @Transactional
    public ProductCategory saveProductCategory(ProductCategory productCategory) {
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
            return productCategoryRepository.save(productCategory);
        }
        return null;
    }
}
