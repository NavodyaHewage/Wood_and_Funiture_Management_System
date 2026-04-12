package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.ReceiptDTO;
import com.group_project.wfms_backend.model.Receipt;
import com.group_project.wfms_backend.dto.auth.ReceiptDetailsDTO;
import com.group_project.wfms_backend.model.*;
import com.group_project.wfms_backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReceiptService {
    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired private ReceiptDetailsRepository receiptDetailsRepository;
    @Autowired private IncomeAccountRepository incomeAccountRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CustomerOrderDetailsRepository orderDetailsRepo;

    @Transactional
    public void createFullReceipt(ReceiptDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        User creator = userRepository.findById(dto.getCreatedById())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 1. Save Receipt
        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(dto.getReceiptNumber());
        receipt.setDate(dto.getDate());
        receipt.setPaymentMethod(PaymentMethod.valueOf(dto.getPaymentMethod()));
        receipt.setCustomer(customer);
        receipt.setTotalAmount(dto.getTotalAmount());
        receipt.setCreatedBy(creator);
        receipt.setRemarks(dto.getRemarks());

        Receipt savedReceipt = receiptRepository.save(receipt);

        // 2. Save Receipt Details (Loop)
        if (dto.getDetails() != null) {
            for (ReceiptDetailsDTO detailDto : dto.getDetails()) {
                ReceiptDetails details = new ReceiptDetails();
                details.setReceipt(savedReceipt);
                details.setAmount(detailDto.getAmount());

                CustomerOrderDetails orderDetail = orderDetailsRepo.findById(detailDto.getCustomerOrderDetailsId())
                        .orElseThrow(() -> new EntityNotFoundException("Order details not found"));
                details.setCustomerOrderDetails(orderDetail);

                receiptDetailsRepository.save(details);
            }
        }

        // 3. Auto-Save to Income Account
        IncomeAccount income = new IncomeAccount();
        income.setDate(savedReceipt.getDate());
        income.setAmount(savedReceipt.getTotalAmount());
        income.setDescription("Sales Income from Receipt: " + savedReceipt.getReceiptNumber());
        income.setReceipt(savedReceipt);
        income.setCreatedBy(creator);

        incomeAccountRepository.save(income);
    }
}
