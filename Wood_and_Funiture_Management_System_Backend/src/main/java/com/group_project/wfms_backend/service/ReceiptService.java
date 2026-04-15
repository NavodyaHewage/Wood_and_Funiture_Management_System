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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final CustomerRepository customerRepository;
    private final CustomerOrderDetailsRepository orderDetailsRepository;
    private final UserRepository userRepository;
    private final IncomeAccountRepository incomeAccountRepository;

    // ── CREATE ───────────────────────────────────────────────
    @Transactional
    public ReceiptResponseDTO createReceipt(ReceiptRequestDTO dto) {
        if (receiptRepository.existsByReceiptNumber(dto.getReceiptNumber())) {
            throw new RuntimeException("Receipt number already exists: " + dto.getReceiptNumber());
        }

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found: " + dto.getCustomerId()));

        // Resolve creator early so it's available for IncomeAccount
        User creator = null;
        if (dto.getCreatedById() != null) {
            creator = userRepository.findById(dto.getCreatedById())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(dto.getReceiptNumber());
        receipt.setDate(dto.getDate());
        receipt.setPaymentMethod(
                PaymentMethod.valueOf(dto.getPaymentMethod().toUpperCase().replace(" ", "_"))
        );
        receipt.setCustomer(customer);
        receipt.setTotalAmount(dto.getTotalAmount());
        receipt.setRemarks(dto.getRemarks());
        receipt.setCreatedBy(creator);

        List<ReceiptDetails> details = dto.getReceiptDetails().stream().map(d -> {
            CustomerOrderDetails orderDetail = orderDetailsRepository.findById(d.getCustomerOrderDetailsId())
                    .orElseThrow(() -> new RuntimeException("Order detail not found: " + d.getCustomerOrderDetailsId()));
            ReceiptDetails rd = new ReceiptDetails();
            rd.setReceipt(receipt);
            rd.setCustomerOrderDetails(orderDetail);
            rd.setAmount(d.getAmount());
            return rd;
        }).collect(Collectors.toList());

        receipt.setReceiptDetails(details);

        // Save receipt first to get the persisted entity
        Receipt savedReceipt = receiptRepository.save(receipt); // ← SAVE BEFORE using savedReceipt

        // Auto-Save to Income Account
        IncomeAccount income = new IncomeAccount();
        income.setDate(savedReceipt.getDate());
        income.setAmount(savedReceipt.getTotalAmount());
        income.setDescription("Sales Income from Receipt: " + savedReceipt.getReceiptNumber());
        income.setReceipt(savedReceipt);
        income.setCreatedBy(creator);

        incomeAccountRepository.save(income);

        return toResponseDTO(savedReceipt); // ← Use savedReceipt, not re-saving
    }

    // ── READ ALL ─────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<ReceiptResponseDTO> getAllReceipts() {
        return receiptRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
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

        if (receipt.getCreatedBy() != null) {
            dto.setCreatedById(receipt.getCreatedBy().getUserId().longValue());
        }

        if (receipt.getReceiptDetails() != null) {
            dto.setReceiptDetails(receipt.getReceiptDetails().stream().map(rd -> {
                ReceiptDetailDTO detail = new ReceiptDetailDTO();//methnadi mama receiptdetail walata wenma class ekka hduwane ethkot ara code eka reciptsresponse class eka athule naha
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
