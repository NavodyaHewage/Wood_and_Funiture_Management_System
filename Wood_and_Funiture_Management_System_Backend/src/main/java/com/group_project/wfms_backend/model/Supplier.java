package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Supplier")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Sup_id")
    private Integer supId;

    @Column(name = "Sup_name", nullable = false, length = 200)
    private String supName;

    @Column(name = "Sup_Cat", length = 100)
    private String supCat;

    @Column(name = "Mobile", nullable = false, length = 15)
    private String mobile;

    @Column(name = "Address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "Email", length = 100)
    private String email;

    @CreationTimestamp
    @Column(name = "Created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "Is_active")
    private Boolean isActive = true;
}
