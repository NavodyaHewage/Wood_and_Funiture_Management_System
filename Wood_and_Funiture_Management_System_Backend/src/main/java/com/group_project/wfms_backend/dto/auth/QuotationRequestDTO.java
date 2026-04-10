package com.group_project.wfms_backend.dto.auth;



import com.group_project.wfms_backend.model.QuotationStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotationRequestDTO {

    @NotNull(message = "Customer ID is required")
    private Integer customerId;

    private BigDecimal totalAmount;

    private QuotationStatus status;

    @NotNull(message = "Quotation date is required")
    private LocalDate quotationDate;

    private LocalDate validUntil;

    private Integer createdBy;

    private String remarks;

    @Valid
    private List<QuotationDetailsRequestDTO> details;
}