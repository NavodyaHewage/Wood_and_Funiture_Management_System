package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.EmployeeSalaryPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EmployeeSalaryPaymentRepository extends JpaRepository<EmployeeSalaryPayment,Integer> {
    // යම්කිසි වැටුප් වාර්තාවකට (Salary Details ID) අදාළ සියලුම ගෙවීම් ලබා ගැනීමට
    List<EmployeeSalaryPayment> findBySalaryDetails_SalaryDetailsId(Integer salaryDetailsId);
}
