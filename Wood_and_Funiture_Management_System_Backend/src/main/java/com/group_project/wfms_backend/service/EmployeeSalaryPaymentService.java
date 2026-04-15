package com.group_project.wfms_backend.service;


import com.group_project.wfms_backend.dto.auth.EmployeeSalaryPaymentDTO;
import com.group_project.wfms_backend.model.EmployeeSalaryDetails;
import com.group_project.wfms_backend.model.EmployeeSalaryPayment;
import com.group_project.wfms_backend.repository.EmployeeSalaryPaymentRepository;
import com.group_project.wfms_backend.repository.EmployeeSalaryRepository;
import com.group_project.wfms_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeSalaryPaymentService {

    @Autowired
    private EmployeeSalaryPaymentRepository paymentRepository;
    @Autowired private EmployeeSalaryRepository salaryDetailsRepository;
    @Autowired private UserRepository userRepository;

    // 1. CREATE PAYMENT - ගෙවීමක් සිදුකිරීම සහ වැටුප් වාර්තාව Update කිරීම
    @Transactional
    public EmployeeSalaryPaymentDTO makePayment(EmployeeSalaryPaymentDTO dto) {

        // අදාළ Salary Details record එක සෙවීම
        EmployeeSalaryDetails details = salaryDetailsRepository.findById(dto.getSalaryDetailsId())
                .orElseThrow(() -> new EntityNotFoundException("Salary details record not found"));

        // ගෙවීම් වාර්තාව සෑදීම
        EmployeeSalaryPayment payment = new EmployeeSalaryPayment();
        payment.setSalaryDetails(details);
        payment.setDate(dto.getDate());
        payment.setAmount(dto.getAmount());
        payment.setRemarks(dto.getRemarks());

        // Paid By User සැකසීම
        if (dto.getPaidById() != null) {
            userRepository.findById(dto.getPaidById()).ifPresent(payment::setPaidBy);
        }

        // --- වැදගත්: Salary Details වල Paid Amount එක Update කිරීම ---
        BigDecimal currentPaid = details.getPaidAmount() != null ? details.getPaidAmount() : BigDecimal.ZERO;
        details.setPaidAmount(currentPaid.add(dto.getAmount()));
        salaryDetailsRepository.save(details); // Balance Amount එක DB එකෙන් auto ගණනය වේ

        EmployeeSalaryPayment savedPayment = paymentRepository.save(payment);
        return convertToDTO(savedPayment);
    }

    // 2. READ HISTORY - යම් සේවකයෙකුගේ මාසික වැටුපකට අදාළ ගෙවීම් ඉතිහාසය
    public List<EmployeeSalaryPaymentDTO> getPaymentHistory(Integer salaryDetailsId) {
        return paymentRepository.findBySalaryDetails_SalaryDetailsId(salaryDetailsId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private EmployeeSalaryPaymentDTO convertToDTO(EmployeeSalaryPayment entity) {
        EmployeeSalaryPaymentDTO dto = new EmployeeSalaryPaymentDTO();
        dto.setEmpSalaryPaymentId(entity.getEmpSalaryPaymentId());
        dto.setSalaryDetailsId(entity.getSalaryDetails().getSalaryDetailsId());
        dto.setDate(entity.getDate());
        dto.setAmount(entity.getAmount());
        dto.setPaymentMethod(entity.getPaymentMethod().name());
        dto.setRemarks(entity.getRemarks());
        dto.setCreatedDate(entity.getCreatedDate());
        if (entity.getPaidBy() != null) {
            dto.setPaidById(entity.getPaidBy().getUserId());
        }
        return dto;
}
}
