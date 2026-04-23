package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.CustomerOrderRequestDTO;
import com.group_project.wfms_backend.dto.auth.CustomerOrderResponseDTO;
import com.group_project.wfms_backend.model.*;
import com.group_project.wfms_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerOrderService {

    private final CustomerOrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final UserRepository userRepository;

    // ── CREATE ───────────────────────────────────────────────
    @Transactional
    public CustomerOrderResponseDTO createOrder(CustomerOrderRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found: " + dto.getCustomerId()));

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setReceiptNumber(dto.getReceiptNumber());
        order.setPaidAmount(dto.getPaidAmount() != null ? dto.getPaidAmount() : BigDecimal.ZERO);
        order.setOrderDate(dto.getOrderDate());

        if (dto.getCreatedById() != null) {
            User user = userRepository.findById(dto.getCreatedById())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            order.setCreatedBy(user);
        }

        // Map order details
        List<CustomerOrderDetails> details = dto.getOrderDetails().stream().map(d -> {
            ProductCategory cat = productCategoryRepository.findById(d.getProductCatId())
                    .orElseThrow(() -> new RuntimeException("Product category not found: " + d.getProductCatId()));
            CustomerOrderDetails detail = new CustomerOrderDetails();
            detail.setOrder(order);
            detail.setProductCategory(cat);
            detail.setName(d.getName());
            detail.setQuantity(d.getQuantity());
            detail.setPrice(d.getPrice());
            return detail;
        }).collect(Collectors.toList());

        order.setOrderDetails(details);

        // Calculate total
        BigDecimal total = details.stream()
                .map(d -> d.getQuantity().multiply(d.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        return toResponseDTO(orderRepository.save(order));
    }

    // ── READ ALL ─────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CustomerOrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ── READ BY ID ───────────────────────────────────────────
    @Transactional(readOnly = true)
    public CustomerOrderResponseDTO getOrderById(Long id) {
        CustomerOrder order = orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return toResponseDTO(order);
    }

    // ── READ BY CUSTOMER ─────────────────────────────────────
    @Transactional(readOnly = true)
    public List<CustomerOrderResponseDTO> getOrdersByCustomer(Integer customerId) {
        return orderRepository.findByCustomer_CusId(customerId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ── UPDATE ───────────────────────────────────────────────
    @Transactional
    public CustomerOrderResponseDTO updateOrder(Long id, CustomerOrderRequestDTO dto) {
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));

        if (dto.getPaidAmount() != null) order.setPaidAmount(dto.getPaidAmount());
        if (dto.getReceiptNumber() != null) order.setReceiptNumber(dto.getReceiptNumber());
        if (dto.getOrderDate() != null) order.setOrderDate(dto.getOrderDate());
        if (dto.getStatus() != null) {
            try {
                order.setStatus(OrderStatus.valueOf(dto.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                order.setStatus(OrderStatus.PENDING);
            }
        }

        // Replace order details if provided
        if (dto.getOrderDetails() != null && !dto.getOrderDetails().isEmpty()) {
            order.getOrderDetails().clear();

            List<CustomerOrderDetails> newDetails = dto.getOrderDetails().stream().map(d -> {
                ProductCategory cat = productCategoryRepository.findById(d.getProductCatId())
                        .orElseThrow(() -> new RuntimeException("Product category not found"));
                CustomerOrderDetails detail = new CustomerOrderDetails();
                detail.setOrder(order);
                detail.setProductCategory(cat);
                detail.setName(d.getName());
                detail.setQuantity(d.getQuantity());
                detail.setPrice(d.getPrice());
                return detail;
            }).collect(Collectors.toList());

            order.getOrderDetails().addAll(newDetails);

            BigDecimal total = newDetails.stream()
                    .map(d -> d.getQuantity().multiply(d.getPrice()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(total);
        }

        return toResponseDTO(orderRepository.save(order));
    }

    // ── DELETE ───────────────────────────────────────────────
    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }

    // ── MAPPER ───────────────────────────────────────────────
    private CustomerOrderResponseDTO toResponseDTO(CustomerOrder order) {
        CustomerOrderResponseDTO dto = new CustomerOrderResponseDTO();
        dto.setOrderId(order.getOrderId());
        
        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getCusId());
            dto.setCustomerName(order.getCustomer().getCusName());
        }
        
        dto.setReceiptNumber(order.getReceiptNumber());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setPaidAmount(order.getPaidAmount());
        dto.setBalanceAmount(order.getBalanceAmount());
        dto.setStatus(order.getStatus() != null ? order.getStatus().name() : null);
        dto.setOrderDate(order.getOrderDate());

        if (order.getOrderDetails() != null) {
            dto.setOrderDetails(order.getOrderDetails().stream().map(d -> {
                CustomerOrderResponseDTO.OrderDetailDTO dd = new CustomerOrderResponseDTO.OrderDetailDTO();
                dd.setDetailId(d.getId());
                if (d.getProductCategory() != null) {
                    dd.setProductCatId(d.getProductCategory().getId());
                    dd.setProductCatName(d.getProductCategory().getMaterialCategory());
                }
                dd.setName(d.getName());
                dd.setQuantity(d.getQuantity());
                dd.setPrice(d.getPrice());
                dd.setLineTotal(d.getLineTotal());
                return dd;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}