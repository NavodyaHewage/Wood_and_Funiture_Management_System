package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Employee_Salary_details")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Employeesalarydetails {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "Salary_details_id")
        private Integer salaryDetailsId;

        // Many salary records belong to one employee
        @ManyToOne
        @JoinColumn(name = "Employee_id", nullable = false)
        private Employee employee;

        @Column(name = "Month", nullable = false)
        private Integer month;

        @Column(name = "Year", nullable = false)
        private Integer year;

        @Column(name = "Total_Amount")
        private BigDecimal totalAmount;

        @Column(name = "Paid_Amount")
        private BigDecimal paidAmount;

        // Database calculated column
        @Column(name = "Balance_Amount", insertable = false, updatable = false)
        private BigDecimal balanceAmount;

        // ENUM mapping
        @Enumerated(EnumType.STRING)
        @Column(name = "Status")
        private Salary_details_Status status;
    }

}
