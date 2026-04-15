package com.group_project.wfms_backend.model;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "RAW_Material_Cutting_Fee")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RawMaterialCuttingFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Supply_id", nullable = false)
    private SupplyRawMaterial supplyRawMaterial;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Employee_id", nullable = false)
    private Employee employee;

    @Column(name = "Fee", precision = 10, scale = 2, nullable = false)
    private BigDecimal fee;

    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Column(name = "Remarks", columnDefinition = "TEXT")
    private String remarks;
}