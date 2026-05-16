package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.GRN;
import com.group_project.wfms_backend.model.SupplyRawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GRNRepository extends JpaRepository<GRN,Integer> {
    Optional<GRN> findBySupplyOrder(SupplyRawMaterial supplyOrder);
    Optional<GRN> findByInvoiceNumber(String invoiceNumber);
}
