//package com.group_project.wfms_backend.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Table(name="Product_Category")
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class ProductCategory {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "Product_Cat_name",nullable = false,length =150)
//    private int ProductCatid;
//
//    @Column(name="Material_Category", length =100)
//    private String materialCategory;
//
//    @Column(name="Description",columnDefinition="Text")
//    private String Description;
//
//    @Enumerated(EnumType.STRING)
//    @Column(name="Unit_of_Measurement")
//    private UnitOfMeasurement unitOfMeasurement = UnitOfMeasurement.SQUARE_FEET;
//}
package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name="product_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Product_Cat_id", nullable = false)
    private int productCatId;

    @Column(name="Product_Cat_name", length = 150)
    private String productCatName;

    @Column(name="Material_Category", length = 100)
    private String materialCategory;

    @Column(name="Description", columnDefinition="TEXT")
    private String description;

    @Column(name="Unit_of_Measurement")
    private UnitOfMeasurement unitOfMeasurement = UnitOfMeasurement.SQUARE_FEET;

    @Column(name="unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;
}