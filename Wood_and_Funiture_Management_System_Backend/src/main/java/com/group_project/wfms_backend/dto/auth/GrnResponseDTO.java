package com.group_project.wfms_backend.dto.auth;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GrnResponseDTO {
    private Integer grnId;
    private String grnNumber;
    private String invoiceNumber;
    private LocalDate date;
    private BigDecimal amount;
    private BigDecimal totalAmount;
    private String remarks;
    private String createdByName;
    
    // Supplier Details
    private Integer supplierId;
    private String supplierName;
    private String supplierAddress;
    private String supplierMobile;
    private String supplierEmail;
    
    // Supply Order Details
    private Integer supplyOrderId;
    private String supplyOrderInvoiceNumber;
    private BigDecimal transportCost;
    private BigDecimal cuttingFee;
    
    private LocalDateTime createdAt;
    private List<GrnDetailResponseDTO> grnDetails;
}