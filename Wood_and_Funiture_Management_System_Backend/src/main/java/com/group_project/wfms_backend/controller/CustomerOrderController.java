package com.group_project.wfms_backend.controller;


import com.group_project.wfms_backend.dto.auth.AddOrderRequestDTO;
import com.group_project.wfms_backend.model.CustomerOrder;
import com.group_project.wfms_backend.service.CustomerOrderService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/orders")

public class CustomerOrderController {

//    @Autowired
//    private CustomerOrderService customerOrderService;
//
//    @PostMapping("/add")
//    public ResponseEntity<CustomerOrderDetails> addCustomerOrder(@RequestBody CustomerOrderDetails customerOrderDetails){
//        @RequestBody AddOrderRequestDTO dto);
//
//        CustomerOrder order = new CustomerOrder();
//        order.setCustomer(dto.getCustomer());
//        order.setOrderDate(LocalDate.now());
//        order.setStatus(OrderStatus.Pending);
//        order.setTotalAmount(dto.getTotalAmount());
//        order.setPaidAmount(dto.getPaidAmount());
//        order.setCreatedBy(dto.getCreatedBy());
//
//        orderService.createOrder(order, dto.getDetails());
//        return ResponseEntity.ok("Customer Order Added Successfully");
    }

















    }



