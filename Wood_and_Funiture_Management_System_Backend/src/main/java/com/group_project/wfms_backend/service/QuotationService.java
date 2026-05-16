package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.QuotationDetailsRequestDTO;
import com.group_project.wfms_backend.dto.auth.QuotationDetailsResponseDTO;
import com.group_project.wfms_backend.dto.auth.QuotationRequestDTO;
import com.group_project.wfms_backend.dto.auth.QuotationResponseDTO;
import com.group_project.wfms_backend.model.*;
import com.group_project.wfms_backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final QuotationDetailsRepository quotationDetailsRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductStockRepository productStockRepository;
    private final CustomerOrderRepository customerOrderRepository;

    public QuotationResponseDTO createQuotation(QuotationRequestDTO requestDTO) {
        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + requestDTO.getCustomerId()));

        User createdBy = null;
        if (requestDTO.getCreatedBy() != null) {
           createdBy = userRepository.findById(requestDTO.getCreatedBy())
                   .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + requestDTO.getCreatedBy()));//==== quatation create (id eka 1 weela fix)krnkota ena erorr ekaka nisa meka ordelsethrow maru kara




        }

        Quotation quotation = new Quotation();
        quotation.setCustomer(customer);
        quotation.setQuotationDate(requestDTO.getQuotationDate());
        quotation.setValidUntil(requestDTO.getValidUntil());
        quotation.setStatus(requestDTO.getStatus() != null ? requestDTO.getStatus() : QuotationStatus.PENDING);
        quotation.setRemarks(requestDTO.getRemarks());
        quotation.setCreatedBy(createdBy);

        // Generate Quotation Number
        quotation.setQuotationNumber(quotationRepository.generateQuotationNumber());

        if (requestDTO.getDetails() != null && !requestDTO.getDetails().isEmpty()) {
            List<QuotationDetails> detailsList = mapToDetailEntities(requestDTO.getDetails(), quotation);
            quotation.setDetails(detailsList);
            quotation.setTotalAmount(calculateTotal(detailsList));
        } else {
            quotation.setTotalAmount(BigDecimal.ZERO);
        }

        return mapToResponseDTO(quotationRepository.save(quotation));
    }

    @Transactional(readOnly = true)
    public QuotationResponseDTO getQuotationById(Integer quotationId) {
        return mapToResponseDTO(findQuotationById(quotationId));
    }

    @Transactional(readOnly = true)
    public List<QuotationResponseDTO> getAllQuotations() {
        return quotationRepository.findAll()
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public QuotationResponseDTO updateQuotation(Integer id, QuotationRequestDTO requestDTO) {
        Quotation existing = findQuotationById(id);

        if (existing.getStatus() == QuotationStatus.CONVERTED) {
            throw new IllegalStateException("Cannot update a quotation that has already been converted to an order.");
        }

        Customer customer = customerRepository.findById(requestDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + requestDTO.getCustomerId()));

        existing.setCustomer(customer);
        existing.setQuotationDate(requestDTO.getQuotationDate());
        existing.setValidUntil(requestDTO.getValidUntil());
        if (requestDTO.getStatus() != null) {
            existing.setStatus(requestDTO.getStatus());
        }
        existing.setRemarks(requestDTO.getRemarks());

        existing.getDetails().clear();
        if (requestDTO.getDetails() != null) {
            List<QuotationDetails> newDetails = mapToDetailEntities(requestDTO.getDetails(), existing);
            existing.getDetails().addAll(newDetails);
            existing.setTotalAmount(calculateTotal(newDetails));
        } else {
            existing.setTotalAmount(BigDecimal.ZERO);
        }

        return mapToResponseDTO(quotationRepository.save(existing));
    }

    public void deleteQuotation(Integer id) {
        Quotation existing = findQuotationById(id);
        if (existing.getStatus() == QuotationStatus.CONVERTED) {
            throw new IllegalStateException("Cannot delete a quotation that has already been converted to an order.");
        }
        quotationRepository.delete(existing);
    }

    public QuotationResponseDTO updateQuotationStatus(Integer id, QuotationStatus status) {
        Quotation existing = findQuotationById(id);
        existing.setStatus(status);
        return mapToResponseDTO(quotationRepository.save(existing));
    }

    @Transactional
    public void convertToOrder(Integer quotationId) {
        Quotation quotation = findQuotationById(quotationId);

        // 1. Check if already converted or ready
        if (quotation.getStatus() == QuotationStatus.CONVERTED) {
            throw new IllegalStateException("Quotation already converted to order.");
        }

        // 2. Check and Deduct Stock
        for (QuotationDetails detail : quotation.getDetails()) {
            ProductStock stock = productStockRepository.findByProductCategory_ProductCatId(detail.getProductCategory().getProductCatId())
                    .orElseThrow(() -> new RuntimeException("Stock record not found for category: " + detail.getProductCategory().getMaterialCategory()));

            if (stock.getAvailableQuantity().compareTo(detail.getQuantity()) < 0) {
                throw new RuntimeException("Insufficient stock for: " + detail.getProductCategory().getMaterialCategory() 
                    + ". Available: " + stock.getAvailableQuantity() + ", Required: " + detail.getQuantity());
            }

            // Deduct stock
            stock.setAvailableQuantity(stock.getAvailableQuantity().subtract(detail.getQuantity()));
            productStockRepository.save(stock);
        }

        // 3. Update Quotation Status
        quotation.setStatus(QuotationStatus.CONVERTED);
        quotationRepository.save(quotation);

        // 4. Create Customer Order
        CustomerOrder order = new CustomerOrder();
        order.setCustomer(quotation.getCustomer());
        order.setOrderNumber(customerOrderRepository.generateOrderNumber());
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(quotation.getTotalAmount());
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedBy(quotation.getCreatedBy());
        
        // Set the quotation number for tracking
        order.setQuotationNumber(quotation.getQuotationNumber() != null ? quotation.getQuotationNumber() : "QTN-" + quotation.getQuotationId());

        CustomerOrder savedOrder = customerOrderRepository.save(order);

        // 5. Create Order Details
        List<CustomerOrderDetails> orderDetails = quotation.getDetails().stream().map(qd -> {
            CustomerOrderDetails od = new CustomerOrderDetails();
            od.setOrder(savedOrder);
            od.setProductCategory(qd.getProductCategory());
            od.setName(qd.getName());
            od.setQuantity(qd.getQuantity());
            od.setPrice(qd.getPrice());
            return od;
        }).collect(Collectors.toList());

        savedOrder.setOrderDetails(orderDetails);
        customerOrderRepository.save(savedOrder);
    }

    private Quotation findQuotationById(Integer id) {
        return quotationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Quotation not found with ID: " + id));
    }

    private List<QuotationDetails> mapToDetailEntities(List<QuotationDetailsRequestDTO> detailsDTO, Quotation quotation) {
        return detailsDTO.stream().map(dto -> {
            ProductCategory category = productCategoryRepository.findById(dto.getProductCatId())
                    .orElseThrow(() -> new EntityNotFoundException("Product Category not found with ID: " + dto.getProductCatId()));
            QuotationDetails detail = new QuotationDetails();
            detail.setQuotation(quotation);
            detail.setProductCategory(category);
            detail.setName(dto.getName());
            detail.setQuantity(dto.getQuantity());
            detail.setPrice(dto.getPrice());
            return detail;
        }).collect(Collectors.toList());
    }

    private BigDecimal calculateTotal(List<QuotationDetails> details) {
        return details.stream()
                .map(d -> d.getQuantity().multiply(d.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private QuotationResponseDTO mapToResponseDTO(Quotation q) {
        QuotationResponseDTO dto = new QuotationResponseDTO();
        dto.setQuotationId(q.getQuotationId());
        dto.setQuotationNumber(q.getQuotationNumber());
        dto.setCustomerId(q.getCustomer().getCusId());
        dto.setCustomerName(q.getCustomer().getCusName());
        dto.setTotalAmount(q.getTotalAmount());
        dto.setStatus(q.getStatus());
        dto.setQuotationDate(q.getQuotationDate());
        dto.setValidUntil(q.getValidUntil());
        dto.setRemarks(q.getRemarks());
        dto.setCreatedBy(q.getCreatedBy() != null ? q.getCreatedBy().getUserId() : null);

        if (q.getDetails() != null) {
            dto.setDetails(q.getDetails().stream().map(this::mapDetailToResponseDTO).collect(Collectors.toList()));
        }
        return dto;
    }

    private QuotationDetailsResponseDTO mapDetailToResponseDTO(QuotationDetails d) {
        QuotationDetailsResponseDTO dto = new QuotationDetailsResponseDTO();
        dto.setDetailsId(d.getDetailsId());
        dto.setProductCatId(d.getProductCategory().getProductCatId());
        dto.setProductCatName(d.getProductCategory().getMaterialCategory());
        dto.setName(d.getName());
        dto.setQuantity(d.getQuantity());
        dto.setPrice(d.getPrice());
        dto.setLineTotal(d.getQuantity().multiply(d.getPrice()));
        return dto;
    }
}