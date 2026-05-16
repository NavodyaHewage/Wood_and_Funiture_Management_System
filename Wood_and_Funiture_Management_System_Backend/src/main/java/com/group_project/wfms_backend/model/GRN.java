package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "GRN")
@Getter
@Setter
@NoArgsConstructor
public class GRN {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GRN_id")
    private Integer grnId;

    @Column(name = "GRN_Number", nullable = false, unique = true, length = 50)
    private String grnNumber;

    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;

    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Column(name = "Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supply_order_id")
    private SupplyRawMaterial supplyOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id")
    private Expenseaccount expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Created_by")
    private User createdBy;

    @Column(name = "Remarks", columnDefinition = "TEXT")
    private String remarks;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<GrnDetails> grnDetails;
}
