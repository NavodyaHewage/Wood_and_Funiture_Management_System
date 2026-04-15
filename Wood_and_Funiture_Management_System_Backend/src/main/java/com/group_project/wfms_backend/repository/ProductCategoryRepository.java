package com.group_project.wfms_backend.repository;





import com.group_project.wfms_backend.model.ProductCategory;
import com.group_project.wfms_backend.model.UnitOfMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

    @Repository
    public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {

       //find karanwa materiailcategory walin
        List<ProductCategory> findByMaterialCategory(String materialCategory);


        List<ProductCategory> findByUnitOfMeasurement(UnitOfMeasurement unitOfMeasurement);

     //
        @Query("SELECT p FROM ProductCategory p WHERE LOWER(p.materialCategory) LIKE LOWER(CONCAT('%', :keyword, '%'))")
        List<ProductCategory> searchByMaterialCategory(@Param("keyword") String keyword);


        boolean existsByMaterialCategory(String materialCategory);

    }

