package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Equity_Account")
@Data
@Getter
@Setter
@NoArgsConstructor
public class EquityAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Equity_id")
    private Integer equityId; // Repo එකේ findById(Integer) නිසා මෙතනත් Integer දීම පහසුයි

    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "Type", nullable = false)
    private EquityType type;

    @Column(name = "Amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Created_by", referencedColumnName = "User_id")
    private User createdBy;
}
