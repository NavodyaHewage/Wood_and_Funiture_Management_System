package com.group_project.wfms_backend.dto.auth;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class GrnResponseDTO {
    private Integer grnId;
    private String grnNumber;
    private LocalDate date;
    private BigDecimal amount;
    private String remarks;
    private String createdByName;
    private List<GrnDetailResponseDTO> grnDetails;
}