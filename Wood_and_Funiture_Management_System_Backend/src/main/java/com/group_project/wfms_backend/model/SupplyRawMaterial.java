package com.group_project.wfms_backend.model;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Supply_RAW_Material")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyRawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Supply_id")
    private Integer supplyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Supplier_id", nullable = false)
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RM_id", nullable = false)
    private RawMaterialItem rawMaterialItem;

    @Column(name = "Invoice_Number", length = 50)
    private String invoiceNumber;

    @Column(name = "Total_Amount", precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "Transport", precision = 10, scale = 2)
    private BigDecimal transport = BigDecimal.ZERO;

    @Column(name = "Cutting_Fee", precision = 10, scale = 2)
    private BigDecimal cuttingFee = BigDecimal.ZERO;

    @Column(name = "Cutting_Fee_id")
    private Integer cuttingFeeId;

    @Column(name = "Supply_Date", nullable = false)
    private LocalDate supplyDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Created_by")
    private User createdBy;

    @OneToMany(mappedBy = "supplyRawMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplyRawMaterialDetails> supplyDetails;

    @OneToMany(mappedBy = "supplyRawMaterial", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RawMaterialCuttingFee> cuttingFees;
}
