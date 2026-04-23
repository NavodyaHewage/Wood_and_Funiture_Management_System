package com.group_project.wfms_backend.dto.auth;

import com.group_project.wfms_backend.model.AttendanceStatus;
import lombok.Data;
import java.time.LocalTime;

@Data
public class AttendanceUpdateDTO {
    private AttendanceStatus status;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private String remarks;
}
