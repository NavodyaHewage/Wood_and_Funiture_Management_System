package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Income_Account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Income_Account_Id")
    private Long id;

    // Add other fields as needed, for now just the ID to satisfy JPA
}
