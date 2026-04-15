package com.group_project.wfms_backend.dto.auth;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmployeeSalaryDTO {
        private Integer salaryDetailsId;
        private Integer employeeId;
        private String employeeName; // UI එකේ පෙන්වීමට පහසු වීමට
        private Integer month;
        private Integer year;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal balanceAmount; // DB එකෙන් generate වන අගය
        private String status;
}
