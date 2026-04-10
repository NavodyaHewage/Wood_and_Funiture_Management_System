package com.group_project.wfms_backend.dto.auth;

import com.group_project.wfms_backend.model.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AttendanceRequest {
    private Integer employeeId;
    private LocalDate date;
    private AttendanceStatus status;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private String remarks;



}
