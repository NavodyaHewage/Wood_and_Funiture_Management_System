package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.CustomerOrderRequestDTO;
import com.group_project.wfms_backend.dto.auth.CustomerOrderResponseDTO;
import com.group_project.wfms_backend.service.CustomerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @PostMapping
    public ResponseEntity<CustomerOrderResponseDTO> createOrder(@RequestBody CustomerOrderRequestDTO dto) {
        return new ResponseEntity<>(customerOrderService.createOrder(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CustomerOrderResponseDTO>> getAllOrders() {
        return ResponseEntity.ok(customerOrderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrderResponseDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(customerOrderService.getOrderById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerOrderResponseDTO>> getOrdersByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(customerOrderService.getOrdersByCustomer(customerId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerOrderResponseDTO> updateOrder(@PathVariable Long id, @RequestBody CustomerOrderRequestDTO dto) {
        return ResponseEntity.ok(customerOrderService.updateOrder(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        customerOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}