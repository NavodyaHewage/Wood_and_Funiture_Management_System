package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.*;
import com.group_project.wfms_backend.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController{@Autowired
private AttendanceService attendanceService;

    // GET /api/attendance/employees?date=2025-07-01
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeAttendanceRow>> getEmployeesForAttendance(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if (date == null) date = LocalDate.now();
        return ResponseEntity.ok(attendanceService.getEmployeesForAttendance(date));
    }

    // POST /api/attendance/mark
    @PostMapping("/mark")
    public ResponseEntity<AttendanceResponse> markAttendance(
            @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.markAttendance(request));
    }

    // POST /api/attendance/bulk-mark
    @PostMapping("/bulk-mark")
    public ResponseEntity<List<AttendanceResponse>> markBulkAttendance(
            @RequestBody BulkAttendanceRequest request) {
        return ResponseEntity.ok(attendanceService.markBulkAttendance(request));
    }

    // GET /api/attendance/by-date?date=2025-07-01
    @GetMapping("/by-date")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.getAttendanceByDate(date));
    }

    // GET /api/attendance/employee/{empId}?from=2025-07-01&to=2025-07-31
    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<AttendanceResponse>> getEmployeeAttendance(
            @PathVariable Integer empId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(attendanceService.getAttendanceByEmployeeAndRange(empId, from, to));
    }

    // GET /api/attendance/summary/{empId}?month=7&year=2025
    @GetMapping("/summary/{empId}")
    public ResponseEntity<AttendanceSummary> getMonthlySummary(
            @PathVariable Integer empId,
            @RequestParam int month,
            @RequestParam int year) {
        return ResponseEntity.ok(attendanceService.getMonthlyAttendanceSummary(empId, month, year));
    }

    // POST /api/attendance/auto-absent  (manual trigger for testing)
    @PostMapping("/auto-absent")
    public ResponseEntity<String> triggerAutoAbsent() {
        attendanceService.autoMarkAbsentForToday();
        return ResponseEntity.ok("Auto-absent marking triggered successfully for today.");
    }
}
