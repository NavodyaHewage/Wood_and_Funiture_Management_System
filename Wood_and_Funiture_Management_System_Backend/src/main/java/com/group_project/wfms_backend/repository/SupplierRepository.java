package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {
    boolean existsByMobile(String mobile);

    boolean existsByNic(String nic);

    Optional<Supplier> findByEmail(String email);
}

