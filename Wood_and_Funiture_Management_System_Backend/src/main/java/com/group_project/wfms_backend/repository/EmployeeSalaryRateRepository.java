package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.EmployeeSalaryRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeSalaryRateRepository extends JpaRepository<EmployeeSalaryRate, Integer> {
    Optional<EmployeeSalaryRate> findByRateNameAndIsActiveTrue(String rateName);
}
