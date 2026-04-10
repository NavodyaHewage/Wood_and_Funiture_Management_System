package com.group_project.wfms_backend.dto.auth;




import com.group_project.wfms_backend.model.QuotationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuotationResponseDTO {

    private Integer quotationId;

    private Integer customerId;
    private String customerName;

    private BigDecimal totalAmount;

    private QuotationStatus status;

    private LocalDate quotationDate;

    private LocalDate validUntil;

    private Integer createdBy;
    private String createdByName;

    private String remarks;

    private List<QuotationDetailsResponseDTO> details;
}
