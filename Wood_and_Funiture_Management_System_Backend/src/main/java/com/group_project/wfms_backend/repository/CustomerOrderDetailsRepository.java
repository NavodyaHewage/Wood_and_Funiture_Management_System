package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.CustomerOrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerOrderDetailsRepository extends JpaRepository<CustomerOrderDetails,Integer> {
}
