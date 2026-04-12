package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Loan_Deduction_Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanDeductionRuleRepository extends JpaRepository<Loan_Deduction_Rule,Integer> {
}
