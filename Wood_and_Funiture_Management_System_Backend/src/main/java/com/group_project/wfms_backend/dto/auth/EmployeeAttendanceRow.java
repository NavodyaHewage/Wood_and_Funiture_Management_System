package com.group_project.wfms_backend.dto.auth;

import com.group_project.wfms_backend.model.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class EmployeeAttendanceRow {
    private Integer employeeId;
    private String fullName;
    private String designation;
    private AttendanceStatus status;
    private boolean alreadyMarked;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private Double overtimeHours;
    private String remarks;



}
