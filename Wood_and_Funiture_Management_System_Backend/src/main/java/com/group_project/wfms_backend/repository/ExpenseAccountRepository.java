package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Expenseaccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface ExpenseAccountRepository extends JpaRepository <Expenseaccount, Integer>{
    // ලාභය ගණනය කිරීමට අවශ්‍ය මුළු වියදම ලබා ගැනීම
    @Query("SELECT SUM(e.amount) FROM Expenseaccount e WHERE e.date BETWEEN :start AND :end")
    BigDecimal getTotalExpense(@Param("start") LocalDate start, @Param("end") LocalDate end);

}
