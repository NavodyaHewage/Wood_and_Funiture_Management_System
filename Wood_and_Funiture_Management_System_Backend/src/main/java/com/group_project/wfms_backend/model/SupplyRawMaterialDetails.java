package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Supply_RAW_Material_Details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyRawMaterialDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Supply_RAW_Material_Details_ID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Supply_id", nullable = false)
    private SupplyRawMaterial supplyRawMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RM_id", nullable = false)
    private RawMaterialItem rawMaterialItem;

    @Column(name = "Log_Number")
    private Integer logNumber;

    @Column(name = "Length_ft", precision = 10, scale = 2, nullable = false)
    private BigDecimal lengthFt;

    @Column(name = "Girth_ft", precision = 10, scale = 2, nullable = false)
    private BigDecimal girthFt;

    // Generated column — (Length * Girth * Girth) / 2304 (Hoppus Formula with Girth in Inches)
    @Column(name = "Total_Quantity_CFT", precision = 10, scale = 3, insertable = false, updatable = false)
    private BigDecimal totalQuantityCft;

    @Column(name = "Price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    // Generated column — ((Length * Girth * Girth) / 2304) * Price
    @Column(name = "Line_Total", precision = 15, scale = 2, insertable = false, updatable = false)
    private BigDecimal lineTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CuttingStatus status = CuttingStatus.PENDING;

    @Column(name = "cut_day")
    private LocalDate cutDay;
}