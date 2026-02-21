package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity



public class CustomerOrder {
    @Id
    @GeneratedValue
    private Long Orderid;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="Customer_id",nullable = true)
    private CustomerOrder customerOrder;

    @Column(name="Receipt_Number",length=50)
    private String rReceiptNumber;

    @Column(name="Total_Amount",length=15)
    private BigDecimal totalAmount;

    @Column(name="Paid_Amount ",precision=15,scale=2)
    private BigDecimal paidAmount=BigDecimal.ZERO;

    @Column(name="balance amount",precision =15,scale=2,insertable=false)
    private BigDecimal balanceAmount;

    @Enumerated(EnumType.STRING)
    @Column(name="status",nullable=false)
    private OrderStatus orderStatus;

    @Column(name="order_date",nullable=false)
    private LocalDate orderDate;

    @ManyToOne(fetch=FetchType.LAZY)//eager ,lazy danakota hoda idea ekk thiyenn oni dana then gena //perforamnece saha behavior controll
    @JoinColumn(name="created_by")
        private CustomerOrder createdBy;

    @OneToMany(mappedBy = "customerOrder",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CustomerOrderDetails> orderDetails;

    public enum orderStatus {
        pending,processing,complete,cancelled
    }

}
