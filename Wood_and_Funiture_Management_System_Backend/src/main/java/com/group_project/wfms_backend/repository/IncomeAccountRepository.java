package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.IncomeAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository

public interface IncomeAccountRepository extends JpaRepository<IncomeAccount,Integer> {

    @Query("SELECT SUM(i.amount) FROM IncomeAccount i WHERE i.date BETWEEN :start AND :end")
    BigDecimal getTotalIncome(@Param("start") LocalDate start, @Param("end") LocalDate end);

}
