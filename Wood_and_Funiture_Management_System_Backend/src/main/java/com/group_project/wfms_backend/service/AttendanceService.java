package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.*;
import com.group_project.wfms_backend.model.AttendanceStatus;
import com.group_project.wfms_backend.model.Employee;
import com.group_project.wfms_backend.model.EmployeeAttendance;
import com.group_project.wfms_backend.repository.EmployeeAttendanceRepository;
import com.group_project.wfms_backend.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j

public class AttendanceService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeAttendanceRepository attendanceRepository;

    public List<EmployeeAttendanceRow> getEmployeesForAttendance(LocalDate date){
    List <Employee> activeEmployees = employeeRepository.findByIsActiveTrue();
    List<EmployeeAttendanceRow> result = new ArrayList<>();
for (Employee emp : activeEmployees) {
    Optional <EmployeeAttendance> existing = attendanceRepository.findByEmployeeAndDate(emp, date);
    EmployeeAttendanceRow row = new EmployeeAttendanceRow();
    row.setEmployeeId(emp.getId());
    row.setFullName(emp.getFullName());
    row.setDesignation(emp.getDesignation());

    if (existing.isPresent()) {
        EmployeeAttendance a = existing.get();
        row.setStatus(a.getStatus());
        row.setAlreadyMarked(true);
        row.setCheckIn(a.getCheckIn());
        row.setCheckOut(a.getCheckOut());
        row.setRemarks(a.getRemarks());
    } else {
        row.setStatus(AttendanceStatus.PRESENT);
        row.setAlreadyMarked(false);
    }

    result.add(row);
}

        return result;
    }

    // -------------------------------------------------------
    // Mark attendance for a single employee
    // -------------------------------------------------------
    @Transactional
    public AttendanceResponse markAttendance(AttendanceRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found: " + request.getEmployeeId()));

        // Update if exists, otherwise create new
        Optional<EmployeeAttendance> existing = attendanceRepository.findByEmployeeAndDate(employee, request.getDate());

        EmployeeAttendance attendance;
        if (existing.isPresent()) {
            attendance = existing.get();
        } else {
            attendance = new EmployeeAttendance();
            attendance.setEmployee(employee);
            attendance.setDate(request.getDate());
        }

        attendance.setStatus(request.getStatus() != null ? request.getStatus() : AttendanceStatus.PRESENT);
        attendance.setCheckIn(request.getCheckIn());
        attendance.setCheckOut(request.getCheckOut());
        attendance.setRemarks(request.getRemarks());

        EmployeeAttendance saved = attendanceRepository.save(attendance);
        log.info("Attendance marked for Employee {} on {}: {}", employee.getFullName(), request.getDate(), attendance.getStatus());

        return mapToResponse(saved);
    }

    // -------------------------------------------------------
    // BULK attendance marking (frontend submits all at once)
    // -------------------------------------------------------
    @Transactional
    public List<AttendanceResponse> markBulkAttendance(BulkAttendanceRequest bulkRequest) {
        List<AttendanceResponse> responses = new ArrayList<>();

        for (AttendanceRequest req : bulkRequest.getAttendanceList()) {
            req.setDate(bulkRequest.getDate());
            responses.add(markAttendance(req));
        }

        log.info("Bulk attendance marked for {} employees on {}", responses.size(), bulkRequest.getDate());
        return responses;
    }

    // -------------------------------------------------------
    // Get attendance list for a specific date
    // -------------------------------------------------------
    public List<AttendanceResponse> getAttendanceByDate(LocalDate date) {
        List<EmployeeAttendance> attendanceList = attendanceRepository.findByDate(date);
        List<AttendanceResponse> result = new ArrayList<>();

        for (EmployeeAttendance a : attendanceList) {
            result.add(mapToResponse(a));
        }

        return result;
    }

    // -------------------------------------------------------
    // Get attendance for an employee in a date range
    // -------------------------------------------------------
    public List<AttendanceResponse> getAttendanceByEmployeeAndRange(Integer empId, LocalDate from, LocalDate to) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + empId));

        List<EmployeeAttendance> attendanceList = attendanceRepository.findByEmployeeAndDateBetween(employee, from, to);
        List<AttendanceResponse> result = new ArrayList<>();

        for (EmployeeAttendance a : attendanceList) {
            result.add(mapToResponse(a));
        }

        return result;
    }

    // -------------------------------------------------------
    // Get monthly attendance summary for an employee
    // -------------------------------------------------------
    public AttendanceSummary getMonthlyAttendanceSummary(Integer empId, int month, int year) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + empId));

        List<Object[]> rawSummary = attendanceRepository.getAttendanceSummary(empId, month, year);

        long present = 0, absent = 0, halfDay = 0, leave = 0;

        for (Object[] row : rawSummary) {
            AttendanceStatus status = (AttendanceStatus) row[0];
            long count = (long) row[1];
            switch (status) {
                case PRESENT  -> present  = count;
                case ABSENT   -> absent   = count;
                case HALF_DAY -> halfDay  = count;
                case LEAVE    -> leave    = count;
            }
        }

        AttendanceSummary summary = new AttendanceSummary();
        summary.setEmployeeId(empId);
        summary.setEmployeeName(employee.getFullName());
        summary.setMonth(month);
        summary.setYear(year);
        summary.setPresentDays(present);
        summary.setAbsentDays(absent);
        summary.setHalfDays(halfDay);
        summary.setLeaveDays(leave);
        summary.setTotalWorkingDays(present + absent + halfDay + leave);

        return summary;
    }

    // -------------------------------------------------------
    // Auto-mark ALL active employees as Absent
    // Called by Spring Scheduler at end of day if not marked
    // -------------------------------------------------------
    @Transactional
    public void autoMarkAbsentForToday() {
        LocalDate today = LocalDate.now();
        List<Employee> activeEmployees = employeeRepository.findByIsActiveTrue();

        int count = 0;
        for (Employee emp : activeEmployees) {
            boolean exists = attendanceRepository.existsByEmployeeAndDate(emp, today);
            if (!exists) {
                EmployeeAttendance attendance = new EmployeeAttendance();
                attendance.setEmployee(emp);
                attendance.setDate(today);
                attendance.setStatus(AttendanceStatus.ABSENT);
                attendance.setRemarks("Auto-marked absent by system");
                attendanceRepository.save(attendance);
                count++;
            }
        }
        log.info("Auto-marked {} employees as Absent for {}", count, today);
    }

    // -------------------------------------------------------
    // Helper: map entity to response DTO
    // -------------------------------------------------------
    private AttendanceResponse mapToResponse(EmployeeAttendance a) {
        AttendanceResponse  response = new AttendanceResponse();
        response.setAttendId(a.getAttendId());
        response.setEmployeeId(a.getEmployee().getId());
        response.setEmployeeName(a.getEmployee().getFullName());
        response.setDate(a.getDate());
        response.setStatus(a.getStatus());
        response.setCheckIn(a.getCheckIn());
        response.setCheckOut(a.getCheckOut());
        response.setRemarks(a.getRemarks());
        return response;
    }
}


