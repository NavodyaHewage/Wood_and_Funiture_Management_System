package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name ="Employee_Salary_Rate")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class EmployeeSalaryRate {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="Rate_id")
    private Integer rateId;

    @Column(name="Rate_Name",nullable=false,length=100)
    private String rateName;

    @Column(name="Amount",nullable=false,precision =10,scale=2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "Rate_Type")
    private SalaryRateType rateType =SalaryRateType.MONTHLY;



}
