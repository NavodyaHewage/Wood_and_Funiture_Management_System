package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseTypeRepository extends JpaRepository<ExpenseType, Integer> {
    Optional<ExpenseType> findByTypeName(String typeName);
}
