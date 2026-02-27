package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cus_id")
    private Integer cusId;

    @Column(name = "Cus_name", nullable = false, length = 200)
    private String cusName;

    @Column(name = "Mobile", nullable = false, length = 15)
    private String mobile;

    @Column(name = "Address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "NIC", length = 20)
    private String nic;

    @CreationTimestamp
    @Column(name = "Created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "Is_active")
    private Boolean isActive = true;
}
