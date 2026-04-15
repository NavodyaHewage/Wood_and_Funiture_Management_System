package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.CustomerOrderRequestDTO;
import com.group_project.wfms_backend.dto.auth.CustomerOrderResponseDTO;
import com.group_project.wfms_backend.service.CustomerOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    public CustomerOrderController(CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    @PostMapping
    public ResponseEntity<CustomerOrderResponseDTO> createOrder(@RequestBody CustomerOrderRequestDTO dto) {
        try {
            CustomerOrderResponseDTO response = customerOrderService.createOrder(dto);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<CustomerOrderResponseDTO>> getAllOrders() {
        try {
            List<CustomerOrderResponseDTO> orders = customerOrderService.getAllOrders();
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrderResponseDTO> getOrderById(@PathVariable Long id) {
        try {
            CustomerOrderResponseDTO order = customerOrderService.getOrderById(id);
            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerOrderResponseDTO>> getOrdersByCustomer(@PathVariable Integer customerId) {
        try {
            List<CustomerOrderResponseDTO> orders = customerOrderService.getOrdersByCustomer(customerId);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerOrderResponseDTO> updateOrder(@PathVariable Long id, @RequestBody CustomerOrderRequestDTO dto) {
        try {
            CustomerOrderResponseDTO response = customerOrderService.updateOrder(id, dto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        try {
            customerOrderService.deleteOrder(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}