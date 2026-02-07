package com.group_project.wfms_backend.controller;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Customer_Order")


public class CustomerOrderController {
    @Id
    @Column(name ="Order_ID")
    private int customer_order_id;

    @ManyToOne
    @JoinColumn(name="Customer_Id")

}
