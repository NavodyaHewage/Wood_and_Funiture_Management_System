package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.EmployeeSalaryPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

@Repository
public interface EmployeeSalaryPaymentRepository extends JpaRepository<EmployeeSalaryPayment, Integer> {
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM EmployeeSalaryPayment p WHERE p.salaryDetails.salaryDetailsId = :salaryDetailsId")
    BigDecimal sumAmountBySalaryDetailsId(@Param("salaryDetailsId") Integer salaryDetailsId);

    // යම්කිසි වැටුප් වාර්තාවකට (Salary Details ID) අදාළ සියලුම ගෙවීම් ලබා ගැනීමට
    List<EmployeeSalaryPayment> findBySalaryDetails_SalaryDetailsId(Integer salaryDetailsId);
}
