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
@RequestMapping("/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<AttendanceResponseDTO> markAttendance(@RequestBody AttendanceCreateDTO dto) {
        return ResponseEntity.ok(attendanceService.markAttendance(dto));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<AttendanceResponseDTO>> markBulkAttendance(@RequestBody List<AttendanceCreateDTO> dtoList) {
        return ResponseEntity.ok(attendanceService.markBulkAttendance(dtoList));
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponseDTO>> getAttendance(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer employeeId) {
        return ResponseEntity.ok(attendanceService.getFilteredAttendance(startDate, endDate, employeeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceResponseDTO> updateAttendance(
            @PathVariable Integer id,
            @RequestBody AttendanceUpdateDTO dto) {
        return ResponseEntity.ok(attendanceService.updateAttendance(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAttendance(@PathVariable Integer id) {
        attendanceService.deleteAttendance(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<AttendanceSummaryDTO> getSummary(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam Integer employeeId) {
        return ResponseEntity.ok(attendanceService.getSummary(month, year, employeeId));
    }
}
