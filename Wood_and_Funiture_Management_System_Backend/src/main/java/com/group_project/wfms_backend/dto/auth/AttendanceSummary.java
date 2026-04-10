package com.group_project.wfms_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AttendanceSummary {
    private Integer employeeId;
    private String employeeName;
    private int month;
    private int year;
    private long presentDays;
    private long absentDays;
    private long halfDays;
    private long leaveDays;
    private long totalWorkingDays;

}
