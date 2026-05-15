package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.SupplyRawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplyRawMaterialRepository extends JpaRepository<SupplyRawMaterial, Integer> {
    List<SupplyRawMaterial> findBySupplier_Email(String email);
}
