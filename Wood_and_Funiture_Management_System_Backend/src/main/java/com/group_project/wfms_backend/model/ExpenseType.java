package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Expence_Type")
@Getter
@Setter
@NoArgsConstructor
public class ExpenseType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Expence_Type_Id")
    private Integer expenseTypeId;

    @Column(name = "Description", nullable = false, length = 200)
    private String description;

}
