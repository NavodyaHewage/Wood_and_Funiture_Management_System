package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.*;
import com.group_project.wfms_backend.exception.*;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AttendanceService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeAttendanceRepository attendanceRepository;

    @Transactional
    public AttendanceResponseDTO markAttendance(AttendanceCreateDTO dto) {
        validateDate(dto.getDate());

        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + dto.getEmployeeId()));

        if (!Boolean.TRUE.equals(employee.getIsActive())) {
            throw new InvalidAttendanceException("Cannot mark attendance for an inactive employee");
        }

        if (attendanceRepository.existsByEmployeeIdAndDate(dto.getEmployeeId(), dto.getDate())) {
            throw new DuplicateAttendanceException("Attendance record already exists for this date");
        }

        validateAttendanceLogic(dto.getStatus(), dto.getCheckIn(), dto.getCheckOut());

        EmployeeAttendance attendance = new EmployeeAttendance();
        attendance.setEmployee(employee);
        attendance.setDate(dto.getDate());
        attendance.setStatus(dto.getStatus());
        attendance.setCheckIn(dto.getCheckIn());
        attendance.setCheckOut(dto.getCheckOut());
        attendance.setRemarks(dto.getRemarks());

        return mapToDTO(attendanceRepository.save(attendance));
    }

    public List<AttendanceResponseDTO> getFilteredAttendance(LocalDate startDate, LocalDate endDate, Integer employeeId) {
        return attendanceRepository.findFilteredAttendance(startDate, endDate, employeeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AttendanceResponseDTO updateAttendance(Integer id, AttendanceUpdateDTO dto) {
        EmployeeAttendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new InvalidAttendanceException("Attendance record not found"));

        validateAttendanceLogic(dto.getStatus(), dto.getCheckIn(), dto.getCheckOut());

        attendance.setStatus(dto.getStatus());
        attendance.setCheckIn(dto.getCheckIn());
        attendance.setCheckOut(dto.getCheckOut());
        attendance.setRemarks(dto.getRemarks());

        return mapToDTO(attendanceRepository.save(attendance));
    }

    @Transactional
    public void deleteAttendance(Integer id) {
        if (!attendanceRepository.existsById(id)) {
            throw new InvalidAttendanceException("Attendance record not found");
        }
        attendanceRepository.deleteById(id);
    }

    public AttendanceSummaryDTO getSummary(int month, int year, Integer employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));

        List<Object[]> rawSummary = attendanceRepository.getAttendanceSummary(employeeId, month, year);

        long present = 0, absent = 0, halfDay = 0, leave = 0, holiday = 0, weekend = 0;
        
        for (Object[] row : rawSummary) {
            AttendanceStatus status = (AttendanceStatus) row[0];
            long count = (long) row[1];
            switch (status) {
                case PRESENT -> present = count;
                case ABSENT -> absent = count;
                case HALF_DAY -> halfDay = count;
                case LEAVE -> leave = count;
                case HOLIDAY -> holiday = count;
                case WEEKEND -> weekend = count;
            }
        }

        AttendanceSummaryDTO summary = new AttendanceSummaryDTO();
        summary.setEmployeeId(employeeId);
        summary.setEmployeeName(employee.getFullName());
        summary.setMonth(month);
        summary.setYear(year);
        summary.setPresentDays(present);
        summary.setAbsentDays(absent);
        summary.setHalfDays(halfDay);
        summary.setLeaveDays(leave);
        summary.setHolidayDays(holiday);
        summary.setWeekendDays(weekend);
        summary.setTotalWorkingDays(present + absent + halfDay + leave + holiday + weekend);

        return summary;
    }

    @Transactional
    public List<AttendanceResponseDTO> markBulkAttendance(List<AttendanceCreateDTO> dtoList) {
        return dtoList.stream()
                .map(this::markAttendance)
                .collect(Collectors.toList());
    }

    private void validateDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new InvalidAttendanceException("Cannot mark attendance for a future date");
        }
    }

    private void validateAttendanceLogic(AttendanceStatus status, LocalTime in, LocalTime out) {
        if (status == AttendanceStatus.ABSENT || status == AttendanceStatus.LEAVE || 
            status == AttendanceStatus.HOLIDAY || status == AttendanceStatus.WEEKEND) {
            if (in != null || out != null) {
                throw new InvalidAttendanceException("Check-in and Check-out must be empty for " + status.getDisplayName() + " status");
            }
        }

        if (status == AttendanceStatus.HALF_DAY && in == null) {
            throw new InvalidAttendanceException("Half Day status requires at least a check-in time");
        }

        if (in != null && out != null && !out.isAfter(in)) {
            throw new InvalidAttendanceException("Check-out time must be after check-in time");
        }
    }

    private AttendanceResponseDTO mapToDTO(EmployeeAttendance a) {
        AttendanceResponseDTO dto = new AttendanceResponseDTO();
        dto.setAttendId(a.getAttendId());
        dto.setEmployeeId(a.getEmployee().getId());
        dto.setEmployeeName(a.getEmployee().getFullName());
        dto.setDate(a.getDate());
        dto.setStatus(a.getStatus());
        dto.setCheckIn(a.getCheckIn());
        dto.setCheckOut(a.getCheckOut());
        dto.setRemarks(a.getRemarks());
        return dto;
    }
}


