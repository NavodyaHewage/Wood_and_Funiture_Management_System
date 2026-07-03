package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.EmployeePaysheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeePaysheetRepository extends JpaRepository<EmployeePaysheet,Integer> {
    Optional<EmployeePaysheet> findByEmployeeIdAndMonthAndYear(Integer employeeId, Integer month, Integer year);

    Optional<EmployeePaysheet> findByEmployeeIdAndDate(Integer employeeId, LocalDate date);

    // එක් සේවකයෙකුගේ සියලුම පේෂීට් වාර්තා ලබා ගැනීමට
    List<EmployeePaysheet> findByEmployeeId(Integer employeeId);
}
