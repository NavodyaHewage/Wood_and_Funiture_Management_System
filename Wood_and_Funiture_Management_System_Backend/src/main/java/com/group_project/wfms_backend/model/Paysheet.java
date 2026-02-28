package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Paysheet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Paysheet_id")
    private Integer paysheetId;

    @Column(name="Paysheet_Number",unique = true,nullable=false,length =50 )
    private String paysheetNumber;

    @Column(name="Month",nullable = false)
    private String month;

    @Column(name = "Year",nullable = false)
    private String year;

    @Column(name = "Total_Employeees",nullable=false)
    private BigDecimal totalEmployees;

    @Column(name="Total_Deductions",precision = 15,scale=2)
    private BigDecimal totalDeductions;

    @Column(name="Total_Salary",precision=15,scale=2)
    private BigDecimal totalSalary=BigDecimal.ZERO;

    @Column(name="Net_Total",precision=15,scale=2)
    private BigDecimal netTotal=BigDecimal.ZERO;

    @Column(name="Generated_Date",nullable=false)
    private String generatedDate;

    @Enumerated(EnumType.STRING)
    @Column(name="Status")
    private PaysheetStatus status = PaysheetStatus.DRAFT;


     
}
