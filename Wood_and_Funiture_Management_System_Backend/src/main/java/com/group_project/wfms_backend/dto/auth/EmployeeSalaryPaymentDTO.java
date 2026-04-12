package com.group_project.wfms_backend.dto.auth;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmployeeSalaryPaymentDTO {
    private Integer empSalaryPaymentId;
    private Integer salaryDetailsId; // අදාළ වැටුප් වාර්තාවේ ID එක
    private LocalDate date;
    private BigDecimal amount;
    private String paymentMethod;
    private Integer paidById; // ගෙවීම සිදුකළ User ගේ ID එක
    private String remarks;
    private LocalDateTime createdDate;


}
