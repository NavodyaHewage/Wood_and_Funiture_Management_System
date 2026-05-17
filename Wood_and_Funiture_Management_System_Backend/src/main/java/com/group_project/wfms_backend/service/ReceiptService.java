package com.group_project.wfms_backend.service;
import com.group_project.wfms_backend.model.PaymentMethod;
import com.group_project.wfms_backend.dto.auth.ReceiptDetailDTO;
import com.group_project.wfms_backend.dto.auth.ReceiptRequestDTO;
import com.group_project.wfms_backend.dto.auth.ReceiptResponseDTO;
import com.group_project.wfms_backend.model.*;
import com.group_project.wfms_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final CustomerRepository customerRepository;
    private final CustomerOrderDetailsRepository orderDetailsRepository;
    private final CustomerOrderRepository orderRepository;
    private final UserRepository userRepository;
    private final IncomeAccountRepository incomeAccountRepository;

    private synchronized String generateSequentialReceiptNumber(LocalDate date) {
        String dateString = date.toString().replace("-", "");
        long count = receiptRepository.countByDate(date);
        
        String number;
        boolean exists;
        long seq = count + 1;
        do {
            number = String.format("RCP-%s-%04d", dateString, seq);
            exists = receiptRepository.existsByReceiptNumber(number);
            if (exists) {
                seq++;
            }
        } while (exists);
        
        return number;
    }

    // ── CREATE ───────────────────────────────────────────────
    @Transactional
    public ReceiptResponseDTO createReceipt(ReceiptRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found: " + dto.getCustomerId()));

        User creator = null;
        if (dto.getCreatedById() != null) {
            creator = userRepository.findById(dto.getCreatedById())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        LocalDate date = dto.getDate() != null ? dto.getDate() : LocalDate.now();
        String receiptNumber = generateSequentialReceiptNumber(date);

        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(receiptNumber);
        receipt.setDate(date);
        receipt.setPaymentMethod(
                PaymentMethod.valueOf(dto.getPaymentMethod().toUpperCase().replace(" ", "_"))
        );
        receipt.setCustomer(customer);
        receipt.setRemarks(dto.getRemarks());
        receipt.setCreatedBy(creator);
        
        receipt.setChequeNumber(dto.getChequeNumber());
        receipt.setBankName(dto.getBankName());
        receipt.setCardType(dto.getCardType());
        receipt.setCardLastDigits(dto.getCardLastDigits());

        BigDecimal sumAllocated = BigDecimal.ZERO;
        List<ReceiptDetails> details = new java.util.ArrayList<>();

        for (ReceiptDetailDTO d : dto.getReceiptDetails()) {
            CustomerOrderDetails orderDetail = orderDetailsRepository.findById(d.getCustomerOrderDetailsId())
                    .orElseThrow(() -> new RuntimeException("Order detail not found: " + d.getCustomerOrderDetailsId()));
            
            BigDecimal lineTotal = orderDetail.getLineTotal() != null ? orderDetail.getLineTotal() : BigDecimal.ZERO;
            BigDecimal paidBefore = orderDetail.getPaidAmount() != null ? orderDetail.getPaidAmount() : BigDecimal.ZERO;
            BigDecimal outstanding = lineTotal.subtract(paidBefore);
            
            if (d.getAmount().compareTo(outstanding) > 0) {
                throw new RuntimeException("Allocated amount " + d.getAmount() + " exceeds line outstanding balance " + outstanding);
            }
            
            ReceiptDetails rd = new ReceiptDetails();
            rd.setReceipt(receipt);
            rd.setCustomerOrderDetails(orderDetail);
            rd.setAmount(d.getAmount());
            details.add(rd);

            orderDetail.setPaidAmount(paidBefore.add(d.getAmount()));
            orderDetailsRepository.save(orderDetail);
            
            sumAllocated = sumAllocated.add(d.getAmount());
        }

        receipt.setReceiptDetails(details);
        receipt.setTotalAmount(sumAllocated);

        Receipt savedReceipt = receiptRepository.save(receipt);

        if (dto.getOrderId() != null) {
            CustomerOrder order = orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new RuntimeException("Order not found: " + dto.getOrderId()));
            
            BigDecimal currentPaid = order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO;
            order.setPaidAmount(currentPaid.add(sumAllocated));
            
            BigDecimal totalAmount = order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal newPaid = order.getPaidAmount();
            
            if (newPaid.compareTo(totalAmount) >= 0) {
                order.setStatus(OrderStatus.COMPLETED);
            } else {
                order.setStatus(OrderStatus.PROCESSING);
            }
            orderRepository.save(order);
        }

        IncomeAccount income = new IncomeAccount();
        income.setDate(savedReceipt.getDate());
        income.setAmount(savedReceipt.getTotalAmount());
        income.setDescription("Sales Income from Receipt: " + savedReceipt.getReceiptNumber());
        income.setReceipt(savedReceipt);
        income.setCreatedBy(creator);

        incomeAccountRepository.save(income);

        return toResponseDTO(savedReceipt);
    }

    // ── READ ALL ─────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ReceiptResponseDTO> getAllReceipts() {
        return receiptRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getReceipts(String search, String sortStr, int page, int limit) {
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "date");
        if ("date_asc".equalsIgnoreCase(sortStr)) {
            sort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "date");
        }
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page - 1, limit, sort);
        Page<Receipt> pageResult = receiptRepository.searchReceipts(search, pageable);
        
        List<ReceiptResponseDTO> data = pageResult.getContent().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
                
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("data", data);
        response.put("total", pageResult.getTotalElements());
        return response;
    }

    // ── READ BY ID ───────────────────────────────────────────
    @Transactional(readOnly = true)
    public ReceiptResponseDTO getReceiptById(Long id) {
        Receipt receipt = receiptRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found: " + id));
        return toResponseDTO(receipt);
    }

    // ── READ BY CUSTOMER ─────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ReceiptResponseDTO> getReceiptsByCustomer(Integer customerId) {
        return receiptRepository.findByCustomer_CusId(customerId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // ── UPDATE ───────────────────────────────────────────────
    @Transactional
    public ReceiptResponseDTO updateReceipt(Long id, ReceiptRequestDTO dto) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found: " + id));

        if (dto.getDate() != null) receipt.setDate(dto.getDate());
        if (dto.getPaymentMethod() != null)
            receipt.setPaymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod()));
        if (dto.getTotalAmount() != null) receipt.setTotalAmount(dto.getTotalAmount());
        if (dto.getRemarks() != null) receipt.setRemarks(dto.getRemarks());

        // Replace details if provided
        if (dto.getReceiptDetails() != null && !dto.getReceiptDetails().isEmpty()) {
            receipt.getReceiptDetails().clear();

            List<ReceiptDetails> newDetails = dto.getReceiptDetails().stream().map(d -> {
                CustomerOrderDetails orderDetail = orderDetailsRepository.findById(d.getCustomerOrderDetailsId())
                        .orElseThrow(() -> new RuntimeException("Order detail not found: " + d.getCustomerOrderDetailsId()));
                ReceiptDetails rd = new ReceiptDetails();
                rd.setReceipt(receipt);
                rd.setCustomerOrderDetails(orderDetail);
                rd.setAmount(d.getAmount());
                return rd;
            }).collect(Collectors.toList());

            receipt.getReceiptDetails().addAll(newDetails);
        }

        return toResponseDTO(receiptRepository.save(receipt));
    }

    // ── DELETE ───────────────────────────────────────────────
    @Transactional
    public void deleteReceipt(Long id) {
        if (!receiptRepository.existsById(id)) {
            throw new RuntimeException("Receipt not found: " + id);
        }
        receiptRepository.deleteById(id);
    }

    // ── MAPPER ───────────────────────────────────────────────
    private ReceiptResponseDTO toResponseDTO(Receipt receipt) {
        ReceiptResponseDTO dto = new ReceiptResponseDTO();
        dto.setReceiptId(receipt.getReceiptId());
        dto.setReceiptNumber(receipt.getReceiptNumber());
        dto.setDate(receipt.getDate());
        dto.setPaymentMethod(receipt.getPaymentMethod() != null ? receipt.getPaymentMethod().name() : null);
        dto.setCustomerId(receipt.getCustomer().getCusId());
        dto.setCustomerName(receipt.getCustomer().getCusName());
        dto.setTotalAmount(receipt.getTotalAmount());
        dto.setRemarks(receipt.getRemarks());
        
        dto.setChequeNumber(receipt.getChequeNumber());
        dto.setBankName(receipt.getBankName());
        dto.setCardType(receipt.getCardType());
        dto.setCardLastDigits(receipt.getCardLastDigits());

        if (receipt.getCreatedBy() != null) {
            dto.setCreatedById(receipt.getCreatedBy().getUserId().longValue());
        }

        if (receipt.getReceiptDetails() != null && !receipt.getReceiptDetails().isEmpty()) {
            CustomerOrderDetails cod = receipt.getReceiptDetails().get(0).getCustomerOrderDetails();
            if (cod != null && cod.getOrder() != null) {
                dto.setOrderId(cod.getOrder().getOrderId());
                dto.setOrderNumber(cod.getOrder().getOrderNumber());
            }
            
            dto.setReceiptDetails(receipt.getReceiptDetails().stream().map(rd -> {
                ReceiptDetailDTO detail = new ReceiptDetailDTO();
                detail.setReceiptDetailsId(rd.getReceiptDetailsId().longValue());
                detail.setCustomerOrderDetailsId(rd.getCustomerOrderDetails().getId());
                detail.setProductName(rd.getCustomerOrderDetails().getName());
                detail.setAmount(rd.getAmount());
                return detail;
            }).collect(Collectors.toList()));
        }

        return dto;
    }

}
