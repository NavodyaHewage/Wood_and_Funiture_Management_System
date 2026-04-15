package com.group_project.wfms_backend.model;

// 6. GRN_Details


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "GRN_Details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrnDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GRN_details_ID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GRN_ID", nullable = false)
    private GRN grn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Supply_RAW_Material_Details_ID", nullable = false)
    private SupplyRawMaterialDetails supplyRawMaterialDetails;

    @Column(name = "GRN_Number", length = 50)
    private String grnNumber;

    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Column(name = "Amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;
}