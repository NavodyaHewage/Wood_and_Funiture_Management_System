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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="product_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_cat_id", nullable = false)
    private int productCatId;

    @Column(name="material_category", length = 100)
    private String materialCategory;

    @Column(name="description", columnDefinition="TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="unit_of_measurement")
    private UnitOfMeasurement unitOfMeasurement = UnitOfMeasurement.SQUARE_FEET;

    @Column(name="unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    // Which raw material types (logs) this category can be cut from - used to filter the
    // Product Category dropdown in the Raw Material Cutting screen by the selected log(s).
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "product_category_raw_material",
        joinColumns = @JoinColumn(name = "product_cat_id"),
        inverseJoinColumns = @JoinColumn(name = "rm_id")
    )
    private List<RawMaterialItem> rawMaterials = new ArrayList<>();
}