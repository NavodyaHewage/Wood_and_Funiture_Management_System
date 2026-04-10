package com.group_project.wfms_backend.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name="Loan_Deduction_Rule")
@Getter
@Setter
@NoArgsConstructor
public class Loan_Deduction_Rule {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "Rule_id")
    private Integer ruleId;

    @ManyToOne
    @JoinColumn(name = "Loan_id", nullable = false)
    private Employee_loan employeeloan;

    @Column(name = "Deduction_Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal deductionAmount;

    @Column(name = "Start_Month", nullable = false)
    private Integer startMonth;

    @Column(name = "Start_Year", nullable = false)
    private Integer startYear;

    @Column(name = "End_Month")
    private Integer endMonth;

    @Column(name = "End_Year")
    private Integer endYear;

    @Column(name = "Is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Created_by")
    private User createdBy;

    @Column(name = "Remarks", columnDefinition = "TEXT")
    private String remarks;
}
