package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.SupplyRawMaterialDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplyRawMaterialDetailsRepository extends JpaRepository<SupplyRawMaterialDetails, Integer> {
}
