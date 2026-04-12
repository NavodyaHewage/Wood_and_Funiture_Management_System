package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.EmployeeSalaryDTO;
import com.group_project.wfms_backend.model.Employee;
import com.group_project.wfms_backend.model.EmployeeSalaryDetails;
import com.group_project.wfms_backend.repository.EmployeeRepository;
import com.group_project.wfms_backend.repository.EmployeeSalaryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class PayrollRecordService {
    @Autowired
    private EmployeeSalaryRepository salaryRepository;
    @Autowired private EmployeeRepository employeeRepository;

    // 1. CREATE - වැටුප් වාර්තාවක් නිර්මාණය කිරීම
    @Transactional
    public EmployeeSalaryDTO createSalaryRecord(EmployeeSalaryDTO dto) {
        // සේවකයා සිටීදැයි පරීක්ෂා කිරීම
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        EmployeeSalaryDetails salary = new EmployeeSalaryDetails();
        salary.setEmployee(employee);
        salary.setMonth(dto.getMonth());
        salary.setYear(dto.getYear());
        salary.setTotalAmount(dto.getTotalAmount());
        salary.setPaidAmount(dto.getPaidAmount());
        // Status එක Entity එකේ default PENDING ලෙස ඇත

        EmployeeSalaryDetails saved = salaryRepository.save(salary);
        return convertToDTO(saved);
    }

    // 2. READ - සියලුම වැටුප් වාර්තා ලබා ගැනීම
    public List<EmployeeSalaryDTO> getAllSalaryRecords() {
        return salaryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 3. READ BY ID - එක් වාර්තාවක් පමණක් ලබා ගැනීම
    public EmployeeSalaryDTO getSalaryById(Integer id) {
        EmployeeSalaryDetails salary = salaryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Record not found"));
        return convertToDTO(salary);
    }

    // Mapping Helper: Entity එක DTO එකකට හැරවීම
    private EmployeeSalaryDTO convertToDTO(EmployeeSalaryDetails entity) {
        EmployeeSalaryDTO dto = new EmployeeSalaryDTO();
        dto.setSalaryDetailsId(entity.getSalaryDetailsId());
        dto.setEmployeeId(entity.getEmployee().getId());
        dto.setEmployeeName(entity.getEmployee().getFullName());
        dto.setMonth(entity.getMonth());
        dto.setYear(entity.getYear());
        dto.setTotalAmount(entity.getTotalAmount());
        dto.setPaidAmount(entity.getPaidAmount());
        dto.setBalanceAmount(entity.getBalanceAmount());
        dto.setStatus(entity.getStatus().name());
        return dto;
    }
}
