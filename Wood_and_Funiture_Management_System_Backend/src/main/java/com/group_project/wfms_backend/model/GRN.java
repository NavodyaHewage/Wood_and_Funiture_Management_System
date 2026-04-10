package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @Column(name = "Date", nullable = false)
    private LocalDate date;

    @Column(name = "Amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Created_by")
    private User createdBy;

    @Column(name = "Remarks", columnDefinition = "TEXT")
    private String remarks;

}
