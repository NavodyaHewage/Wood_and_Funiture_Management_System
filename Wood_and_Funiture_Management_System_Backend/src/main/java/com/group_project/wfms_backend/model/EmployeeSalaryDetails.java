package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Employee_Salary_details",uniqueConstraints = {
        @UniqueConstraint(columnNames = {"Employee_id","Month","Year"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor

public class EmployeeSalaryDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "Salary_details_id")
        private Integer salaryDetailsId;

        //Many salary records belong to one employee
        @ManyToOne
        @JoinColumn(name = "Employee_id", nullable = false)
        private Employee employee;

        @Column(name = "Month", nullable = false)
        private Integer month;

        @Column(name = "Year", nullable = false)
        private Integer year;

//        @Column(name = "Working_Days")
//        private Integer workingDays = 0;
//
//        @Column(name = "Worked_Days")
//        private Integer workedDays = 0;
//
//        @Column(name = "Basic_Salary", precision = 15, scale = 2)
//        private BigDecimal basicSalary = BigDecimal.ZERO;
//
//        @Column(name = "Overtime_Amount", precision = 15, scale = 2)
//        private BigDecimal overtimeAmount = BigDecimal.ZERO;
//
//        @Column(name = "Loan_Deduction", precision = 15, scale = 2)
//        private BigDecimal loanDeduction = BigDecimal.ZERO;
//
//        @Column(name = "Other_Deduction", precision = 15, scale = 2)
//        private BigDecimal otherDeduction = BigDecimal.ZERO;
        @Column(name = "Total_Amount",precision = 15,scale = 2)
        private BigDecimal totalAmount = BigDecimal.ZERO;

        @Column(name = "Paid_Amount",precision = 15,scale = 2)
        private BigDecimal paidAmount = BigDecimal.ZERO;

        // Balance_Amount = Total_Amount - Paid_Amount (generated column in DB)
        @Column(name = "Balance_Amount", insertable = false, updatable = false, precision = 15, scale = 2)
        private BigDecimal balanceAmount;

        // ENUM mapping
        @Enumerated(EnumType.STRING)
        @Column(name = "Status")
        private Salary_details_Status status=Salary_details_Status.PENDING;

       @OneToMany(mappedBy ="salaryDetails",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
       private List<EmployeeSalaryPayment> payments = new ArrayList<>();


        }






