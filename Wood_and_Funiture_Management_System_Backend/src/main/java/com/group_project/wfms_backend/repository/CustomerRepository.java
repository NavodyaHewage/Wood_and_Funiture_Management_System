package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    boolean existsByMobile(String mobile);

    boolean existsByNic(String nic);

    Optional<Customer> findByEmail(String email);


    Optional<Customer> findByMobile(String mobile);


    // 🔹 Search by name
    @Query("SELECT c FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Customer> searchByName(@Param("name") String name);


    // 🔹 Get active customers
    @Query("SELECT c FROM Customer c WHERE c.isActive = true")
    List<Customer> findActiveCustomers();
}


