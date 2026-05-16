package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.SupplyRawMaterialRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group_project.wfms_backend.model.Supplier;
import java.util.List;

@Repository
public interface SupplyRawMaterialRequestRepository extends JpaRepository<SupplyRawMaterialRequest, Integer> {
    List<SupplyRawMaterialRequest> findBySupplier(Supplier supplier);
}
