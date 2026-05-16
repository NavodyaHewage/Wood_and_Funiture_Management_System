package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "supply_raw_material_request_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyRawMaterialRequestDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Request_Detail_id")
    private Integer requestDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Request_id", nullable = false)
    private SupplyRawMaterialRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RM_id", nullable = false)
    private RawMaterialItem rawMaterialItem;

    @Column(name = "Admin_Requested_CFT", nullable = false, precision = 10, scale = 3)
    private BigDecimal adminRequestedCft;

    @Column(name = "Supplier_Approved_CFT", precision = 10, scale = 3)
    private BigDecimal supplierApprovedCft;

    @Column(name = "Unit_Price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "Remarks", columnDefinition = "TEXT")
    private String remarks;
}
