package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.ProductStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductStockRepository extends JpaRepository<ProductStock, Integer> {
    Optional<ProductStock> findByProductCategory_ProductCatId(int productCatId);
}
