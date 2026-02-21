package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Employeerepository extends JpaRepository<Employee,Integer> {
}
