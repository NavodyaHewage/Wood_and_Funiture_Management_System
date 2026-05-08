package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Loan_Deduction_Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoanDeductionRuleRepository extends JpaRepository<Loan_Deduction_Rule, Integer> {
    @Query("SELECT r FROM Loan_Deduction_Rule r WHERE r.employeeloan.employee.id = :employeeId AND r.isActive = true")
    List<Loan_Deduction_Rule> findActiveRulesByEmployeeId(@Param("employeeId") Integer employeeId);
}
