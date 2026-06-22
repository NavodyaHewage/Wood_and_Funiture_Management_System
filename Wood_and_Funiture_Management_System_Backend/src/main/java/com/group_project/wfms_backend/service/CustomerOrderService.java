package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.CustomerOrderRequestDTO;
import com.group_project.wfms_backend.dto.auth.CustomerOrderResponseDTO;
import com.group_project.wfms_backend.dto.auth.OutstandingLinesResponseDTO;
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
    private final ProductStockRepository productStockRepository;


    @Transactional
    public CustomerOrderResponseDTO createOrder(CustomerOrderRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found: " + dto.getCustomerId()));

        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setOrderNumber(orderRepository.generateOrderNumber());
        order.setQuotationNumber(dto.getQuotationNumber());
        order.setPaidAmount(dto.getPaidAmount() != null ? dto.getPaidAmount() : BigDecimal.ZERO);
        order.setOrderDate(dto.getOrderDate());

        if (dto.getCreatedById() != null) {
            User user = userRepository.findById(dto.getCreatedById())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            order.setCreatedBy(user);
        }

        // Map krnwa order details
        List<CustomerOrderDetails> details = dto.getOrderDetails().stream().map(d -> {
            ProductCategory cat = productCategoryRepository.findById(d.getProductCatId())
                    .orElseThrow(() -> new RuntimeException("Product category not found: " + d.getProductCatId()));

            // Check and Deduct Stock
            ProductStock stock = productStockRepository.findByProductCategory_ProductCatId(d.getProductCatId())
                    .orElseThrow(() -> new RuntimeException("Stock record not found for category: " + cat.getMaterialCategory()));

            if (stock.getAvailableQuantity().compareTo(d.getQuantity()) < 0) {
                throw new RuntimeException("Insufficient stock for: " + cat.getMaterialCategory() 
                    + ". Available: " + stock.getAvailableQuantity() + ", Required: " + d.getQuantity());
            }

            stock.setAvailableQuantity(stock.getAvailableQuantity().subtract(d.getQuantity()));
            productStockRepository.save(stock);

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


    @Transactional(readOnly = true)
    public List<CustomerOrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public CustomerOrderResponseDTO getOrderById(Long id) {
        CustomerOrder order = orderRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return toResponseDTO(order);
    }


    @Transactional(readOnly = true)
    public List<CustomerOrderResponseDTO> getOrdersByCustomer(Integer customerId) {
        return orderRepository.findByCustomer_CusId(customerId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

   //update krgnnwa
    @Transactional
    public CustomerOrderResponseDTO updateOrder(Long id, CustomerOrderRequestDTO dto) {
        CustomerOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));

        if (dto.getPaidAmount() != null) order.setPaidAmount(dto.getPaidAmount());
        if (dto.getQuotationNumber() != null) order.setQuotationNumber(dto.getQuotationNumber());
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

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new RuntimeException("Order not found: " + id);
        }
        orderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CustomerOrderResponseDTO> searchOrders(String q) {
        return orderRepository.searchPendingOrProcessingOrders(q).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OutstandingLinesResponseDTO getOutstandingLines(Long orderId) {
        CustomerOrder order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        OutstandingLinesResponseDTO dto = new OutstandingLinesResponseDTO();
        
        OutstandingLinesResponseDTO.OrderSummary summary = new OutstandingLinesResponseDTO.OrderSummary();
        summary.setOrderId(order.getOrderId());
        summary.setOrderNumber(order.getOrderNumber());
        summary.setBalanceAmount(order.getBalanceAmount());
        dto.setOrder(summary);

        List<OutstandingLinesResponseDTO.OutstandingLine> lines = order.getOrderDetails().stream()
                .map(d -> {
                    OutstandingLinesResponseDTO.OutstandingLine line = new OutstandingLinesResponseDTO.OutstandingLine();
                    line.setDetailId(d.getId());
                    if (d.getProductCategory() != null) {
                        line.setProductCatId(d.getProductCategory().getProductCatId());
                    }
                    line.setName(d.getName());
                    line.setQuantity(d.getQuantity());
                    line.setPrice(d.getPrice());
                    
                    BigDecimal calculatedLineTotal = d.getQuantity().multiply(d.getPrice());
                    BigDecimal lineTotal = d.getLineTotal() != null ? d.getLineTotal() : calculatedLineTotal;
                    
                    line.setLineTotal(lineTotal);
                    line.setPaidAmount(d.getPaidAmount() != null ? d.getPaidAmount() : BigDecimal.ZERO);
                    BigDecimal paid = d.getPaidAmount() != null ? d.getPaidAmount() : BigDecimal.ZERO;
                    line.setOutstanding(lineTotal.subtract(paid));
                    return line;
                })
                .filter(l -> l.getOutstanding().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());
        dto.setLines(lines);

        return dto;
    }

    //mpping
    private CustomerOrderResponseDTO toResponseDTO(CustomerOrder order) {
        CustomerOrderResponseDTO dto = new CustomerOrderResponseDTO();
        dto.setOrderId(order.getOrderId());
        dto.setOrderNumber(order.getOrderNumber());

        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getCusId());
            dto.setCustomerName(order.getCustomer().getCusName());
        }

        dto.setQuotationNumber(order.getQuotationNumber());
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
                    dd.setProductCatId(d.getProductCategory().getProductCatId());
                    dd.setProductCatName(d.getProductCategory().getMaterialCategory());
                }
                dd.setName(d.getName());
                dd.setQuantity(d.getQuantity());
                dd.setPrice(d.getPrice());
                dd.setLineTotal(d.getLineTotal() != null ? d.getLineTotal() : d.getQuantity().multiply(d.getPrice()));
                return dd;
            }).collect(Collectors.toList()));
        }

        return dto;
    }
}