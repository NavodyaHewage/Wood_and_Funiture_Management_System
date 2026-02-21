package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.dto.EmployeeNameAndNICView;
import com.group_project.wfms_backend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByNic(String nic);

    Optional<Employee> findByEmail(String email);

    boolean existsByNic(String nic);

    @Query(value = "SELECT * FROM Employee", nativeQuery = true)
    List<Employee> getEmployeeNameAndNIC();
}
