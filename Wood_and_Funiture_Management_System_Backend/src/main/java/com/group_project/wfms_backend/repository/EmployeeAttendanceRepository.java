package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Employee;
import com.group_project.wfms_backend.model.EmployeeAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface EmployeeAttendanceRepository extends JpaRepository<EmployeeAttendance,Integer> {

    Optional<EmployeeAttendance> findByEmployeeAndDate(Employee employee, LocalDate date);

    List<EmployeeAttendance> findByDate(LocalDate date);

    List<EmployeeAttendance> findByEmployeeAndDateBetween(Employee employee, LocalDate from, LocalDate to);

    @Query("SELECT a FROM EmployeeAttendance a WHERE MONTH(a.date) = :month AND YEAR(a.date) = :year")
    List<EmployeeAttendance> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(a) FROM EmployeeAttendance a WHERE a.employee.id = :empId " +
            "AND MONTH(a.date) = :month AND YEAR(a.date) = :year " +
            "AND a.status IN ('PRESENT', 'HALF_DAY')")
    long countPresentDays(@Param("empId") int empId, @Param("month") int month, @Param("year") int year);

    boolean existsByEmployeeIdAndDate(Integer employeeId, LocalDate date);

    @Query("SELECT a FROM EmployeeAttendance a WHERE " +
            "(:startDate IS NULL OR a.date >= :startDate) AND " +
            "(:endDate IS NULL OR a.date <= :endDate) AND " +
            "(:employeeId IS NULL OR a.employee.id = :employeeId)")
    List<EmployeeAttendance> findFilteredAttendance(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("employeeId") Integer employeeId);

    @Query("SELECT a.status, COUNT(a) FROM EmployeeAttendance a " +
            "WHERE a.employee.id = :empId AND MONTH(a.date) = :month AND YEAR(a.date) = :year " +
            "GROUP BY a.status")
    List<Object[]> getAttendanceSummary(@Param("empId") int empId, @Param("month") int month, @Param("year") int year);
}
