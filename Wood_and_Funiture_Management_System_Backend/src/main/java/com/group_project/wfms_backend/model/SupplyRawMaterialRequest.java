package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "supply_raw_material_request")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyRawMaterialRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Request_id")
    private Integer requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Supplier_id", nullable = false)
    private Supplier supplier;

    @CreationTimestamp
    @Column(name = "Request_Date", nullable = false, updatable = false)
    private LocalDateTime requestDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private RequestStatus status = RequestStatus.Pending;

    @Column(name = "Transport_By_Supplier")
    private Boolean transportBySupplier = false;

    @Column(name = "Transport_Notes", columnDefinition = "TEXT")
    private String transportNotes;

    @Column(name = "Remarks", columnDefinition = "TEXT")
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Created_By")
    private User createdBy;

    @Column(name = "Approved_Date")
    private LocalDateTime approvedDate;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplyRawMaterialRequestDetails> details;

    public enum RequestStatus {
        Pending, Approved, Partially_Approved, Rejected, Converted
    }
}
