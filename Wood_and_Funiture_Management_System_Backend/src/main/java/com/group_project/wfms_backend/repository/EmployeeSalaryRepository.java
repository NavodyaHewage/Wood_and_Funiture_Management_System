package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.EmployeeSalaryDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeSalaryRepository extends JpaRepository<EmployeeSalaryDetails,Integer> {
    // සේවකයා, මාසය සහ අවුරුද්ද අනුව record එකක් තිබේදැයි බැලීමට (Duplicate වැළැක්වීමට)
    Optional<EmployeeSalaryDetails> findByEmployeeIdAndMonthAndYear(Integer employeeId, Integer month, Integer year);
}
