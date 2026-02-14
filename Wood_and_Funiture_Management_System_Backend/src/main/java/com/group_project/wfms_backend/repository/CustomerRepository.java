package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsByMobile(String mobile);

    boolean existsByNic(String nic);

    Optional<Customer> findByEmail(String email);
}


