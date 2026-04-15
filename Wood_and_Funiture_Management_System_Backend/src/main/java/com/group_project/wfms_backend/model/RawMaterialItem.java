
package com.group_project.wfms_backend.model;

import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//
//@Entity
//@Table(name = "Row_Material_Item")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class RawMaterialItem {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "RM_id")
//    private Long rmId;
//
//    @Column(name = "RM_name", nullable = false, length = 200)
//    private String rmName;
//
//    @Column(name = "Price_Per_CFT", precision = 10, scale = 2)
//    private BigDecimal pricePerCft;
//
//    @Column(name = "Description", columnDefinition = "TEXT")
//    private String description;
//}



import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "Row_Material_Item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RM_id")
    private Integer rmId;

    @Column(name = "RM_name", nullable = false, length = 200)
    private String rmName;

    @Column(name = "Price_Per_CFT", precision = 10, scale = 2)
    private BigDecimal pricePerCft;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;
}
