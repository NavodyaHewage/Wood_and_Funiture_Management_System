package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Integer> {
    List<ProductCategory> findAllByOrderByNameAsc();


    // 🔹 Search by product name
    @Query("SELECT p FROM ProductCategory p WHERE LOWER(p.productCatName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<ProductCategory> searchProducts(@Param("name") String name);

    // 🔹 Get products by material category
    @Query("SELECT p FROM ProductCategory p WHERE p.materialCategory = :category")
    List<ProductCategory> findByMaterialCategory(@Param("category") String category);

    //get all order by name
    List<ProductCategory> findByOrderByProductCatNameAsc();
}


