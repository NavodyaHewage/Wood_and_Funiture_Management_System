package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.EmployeeSalaryDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeSalaryDetailsRepository extends JpaRepository<EmployeeSalaryDetails, Integer> {

    List<EmployeeSalaryDetails> findByEmployee_Id(Integer employeeId);

    Optional<EmployeeSalaryDetails> findByEmployee_IdAndIsActiveTrue(Integer employeeId);
}
