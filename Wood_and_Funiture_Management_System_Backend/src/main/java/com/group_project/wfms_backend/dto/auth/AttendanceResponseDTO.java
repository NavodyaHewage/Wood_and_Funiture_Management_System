package com.group_project.wfms_backend.dto.auth;

import com.group_project.wfms_backend.model.AttendanceStatus;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceResponseDTO {
    private Integer attendId;
    private Integer employeeId;
    private String employeeName;
    private LocalDate date;
    private AttendanceStatus status;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private String remarks;
}
