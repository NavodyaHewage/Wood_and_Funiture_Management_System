package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;

import java.math.BigDecimal;
import java.util.List;

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

        @Column(name = "Total_Amount",precision = 15,scale = 2)
        private BigDecimal totalAmount = BigDecimal.ZERO;

        @Column(name = "Paid_Amount")
        private BigDecimal paidAmount = BigDecimal.ZERO;

        @Transient
        private BigDecimal balanceAmount;


        // ENUM mapping
        @Enumerated(EnumType.STRING)
        @Column(name = "Status")
        private Salary_details_Status status=Salary_details_Status.PENDING;

        @OneToMany(mappedBy ="salaryDetails",cascade=CascadeType.ALL,fetch=FetchType.LAZY)
        private List<Employeesalarypayment>payments;

        @OneToMany(mappedBy="salaryDetails",cascade =CascadeType.ALL,fetch= FetchType.LAZY)
        private List<Employeeloan>loanDeductions;

        @PostLoad
        public void calculateBalance(){
                if (this.totalAmount != null && this.paidAmount != null) {
                        this.balanceAmount = this.totalAmount.add(this.paidAmount);
                }
        }



    }


