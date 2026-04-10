package com.group_project.wfms_backend.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Asset_Account")
@Getter
@Setter
@NoArgsConstructor
public class AssetAcccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Asset_id")
    private Integer assetId;

    @Column(name = "Asset_Name", nullable = false, length = 200)
    private String assetName;

    @Enumerated(EnumType.STRING)
    @Column(name = "Asset_Type", nullable = false)
    private AssetType assetType;

    @Column(name = "Purchase_Date")
    private LocalDate purchaseDate;

    @Column(name = "Purchase_Value", nullable = false, precision = 15, scale = 2)
    private BigDecimal purchaseValue;

    @Column(name = "Current_Value", precision = 15, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "Depreciation_Rate", precision = 5, scale = 2)
    private BigDecimal depreciationRate = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private AssetStatus status = AssetStatus.Active;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Created_by")
    private User createdBy;
}
