package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByNic(String nic);
    Optional<Employee> findByEmail(String email);
    boolean existsByNic(String nic);

}
