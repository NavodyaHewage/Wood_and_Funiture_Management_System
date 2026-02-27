package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.model.Employee;
import com.group_project.wfms_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    @Transactional
    public Employee createEmployee(Employee employee) {
        if (employeeRepository.existsByNic(employee.getNic())) {
            throw new RuntimeException("Employee with NIC " + employee.getNic() + " already exists");
        }
        return employeeRepository.save(employee);
    }

    @Transactional
    public Employee updateEmployee(Integer id, Employee employeeDetails) {
        Employee employee = getEmployeeById(id);
        employee.setFullName(employeeDetails.getFullName());
        employee.setDesignation(employeeDetails.getDesignation());
        employee.setAddress(employeeDetails.getAddress());
        employee.setNic(employeeDetails.getNic());
        employee.setMobileNumber(employeeDetails.getMobileNumber());
        employee.setEmail(employeeDetails.getEmail());
        employee.setDateJoined(employeeDetails.getDateJoined());
        employee.setIsActive(employeeDetails.getIsActive());
        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(Integer id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }
}
