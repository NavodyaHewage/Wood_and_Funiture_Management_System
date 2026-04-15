package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.PaySheetDTO;
import com.group_project.wfms_backend.model.EmployeePaysheet;
import com.group_project.wfms_backend.model.EmployeeSalaryDetails;
import com.group_project.wfms_backend.repository.EmployeeAttendanceRepository;
import com.group_project.wfms_backend.repository.EmployeePaysheetRepository;
import com.group_project.wfms_backend.repository.EmployeeSalaryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeePaysheetService {
    @Autowired
    private EmployeePaysheetRepository paysheetRepository;
    @Autowired private EmployeeSalaryRepository salaryRepository;
    @Autowired private EmployeeAttendanceRepository attendanceRepository;

    @Transactional
    public void generateAndSavePaysheet(Integer employeeId, Integer month, Integer year) {

        // 1. කලින් හදපු Payroll Record (Salary Details) එක තිබේදැයි බැලීම
        EmployeeSalaryDetails salary = salaryRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year)
                .orElseThrow(() -> new EntityNotFoundException("Salary record not found for this period. Generate Payroll first."));

        // 2. Attendance count එක ලබා ගැනීම
        Long presentDays = attendanceRepository.countPresentDays(employeeId, month, year);

        // 3. පේෂීට් එක දැනටමත් තිබේදැයි බැලීම (තිබේ නම් update කරන්න හෝ skip කරන්න)
        EmployeePaysheet paysheet = paysheetRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year)
                .orElse(new EmployeePaysheet());

        // 4. දත්ත Snapshot එකක් ලෙස ඇතුළත් කිරීම
        paysheet.setEmployee(salary.getEmployee());
        paysheet.setMonth(month);
        paysheet.setYear(year);
        paysheet.setPresentDays(presentDays.intValue());

        // වැටුප් විස්තර (මෙහිදී ඔබගේ calculation logic එකට අනුව වෙනස් කළ හැක)
        paysheet.setTotalEarnings(salary.getTotalAmount());
        paysheet.setNetSalary(salary.getTotalAmount().subtract(salary.getPaidAmount()));

        paysheetRepository.save(paysheet);
    }

    // සේවකයෙකුගේ පේෂීට් එකක් preview බැලීම සඳහා
    public List<PaySheetDTO> getEmployeePaysheets(Integer employeeId) {
        return paysheetRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PaySheetDTO convertToDTO(EmployeePaysheet entity) {
        PaySheetDTO dto = new PaySheetDTO();
        dto.setEmployeeName(entity.getEmployee().getFullName());
        dto.setDesignation(entity.getEmployee().getDesignation());
        dto.setMonth(entity.getMonth());
        dto.setYear(entity.getYear());
        dto.setPresentDays(entity.getPresentDays());
        dto.setTotalEarnings(entity.getTotalEarnings());
        dto.setNetSalary(entity.getNetSalary());
        return dto;
    }
}
