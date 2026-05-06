package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.EmployeeLoanDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface EmployeeLoanDetailsRepository extends JpaRepository<EmployeeLoanDetails, Integer> {
    List<EmployeeLoanDetails> findByLoan_LoanId(Integer loanId);
}
