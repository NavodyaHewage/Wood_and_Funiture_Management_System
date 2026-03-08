package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Length;

@Entity
@Table(name="Product_Category")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Product_Cat_name",nullable = false,length =150)
    private int  ProductCatid;


    @Column(name="Material_Category", length =100)
    private String  materialCategory;

    @Column(name="Description",columnDefinition="Text")
    private String Description;

    @Enumerated(EnumType.STRING)
    @Column(name="Unit_of_Measurement")
    private UnitOfMeasurement unitOfMeasurement = UnitOfMeasurement.SQUARE_FEET;

  


}
