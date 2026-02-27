package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Employee_Salary_payment")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class EmployeeSalaryPayment{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Emp_Salary_Payment_Id")
    private Integer empSalaryPaymentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "Salary_details_id", nullable = false)
    private EmployeeSalaryDetails salaryDetails;

    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Column(name = "Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "Payment_Method")
    private PaymentMethod paymentMethod = PaymentMethod.CASH;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Paid_by")
    private User paidBy;

    @Column(name = "Remarks", columnDefinition = "TEXT")
    private String remarks;
}
