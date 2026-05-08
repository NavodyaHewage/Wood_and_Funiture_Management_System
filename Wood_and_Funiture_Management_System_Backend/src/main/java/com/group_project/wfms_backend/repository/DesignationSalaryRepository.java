package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.DesignationSalary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DesignationSalaryRepository extends JpaRepository<DesignationSalary, Integer> {
    Optional<DesignationSalary> findByDesignationName(String designationName);
    Optional<DesignationSalary> findByDesignationNameAndIsActiveTrue(String designationName);
}
