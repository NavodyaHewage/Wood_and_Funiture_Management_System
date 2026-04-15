package com.group_project.wfms_backend.dto.auth;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class GrnRequestDTO {
    private String grnNumber;
    private LocalDate date;
    private BigDecimal amount;
    private Integer createdById;
    private String remarks;
    private List<GrnDetailRequestDTO> grnDetails;
}