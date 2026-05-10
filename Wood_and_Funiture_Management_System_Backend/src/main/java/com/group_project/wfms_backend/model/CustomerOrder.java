package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Customer_Order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Order_Id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Customer_Id", nullable = false)
    private Customer customer;

    @Column(name = "Quotation_Number", length = 50)
    private String quotationNumber;

    @Column(name = "Total_Amount", precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "Paid_Amount", precision = 15, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    // Generated column - read only
    @Column(name = "Balance_Amount", precision = 15, scale = 2, insertable = false, updatable = false)
    private BigDecimal balanceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "Order_Date", nullable = false)
    private LocalDate orderDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Created_by")
    private User createdBy;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerOrderDetails> orderDetails;


}