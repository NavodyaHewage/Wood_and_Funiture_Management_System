package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.EmployeeLoanDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface EmployeeLoanDetailsRepository extends JpaRepository<EmployeeLoanDetails, Integer> {
    List<EmployeeLoanDetails> findByLoan_LoanId(Integer loanId);

    @Query("SELECT COUNT(d) > 0 FROM EmployeeLoanDetails d WHERE d.loan.loanId = :loanId " +
           "AND FUNCTION('MONTH', d.date) = :month AND FUNCTION('YEAR', d.date) = :year")
    boolean existsByLoanIdAndPeriod(@Param("loanId") Integer loanId, @Param("month") Integer month, @Param("year") Integer year);
}
