package com.group_project.wfms_backend.dto.auth;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
public class EquityAccountDTO {

    private Integer equityId;
    private LocalDate date;
    private String type;
    private BigDecimal amount;
    private String description;
    private Integer userId;
}
