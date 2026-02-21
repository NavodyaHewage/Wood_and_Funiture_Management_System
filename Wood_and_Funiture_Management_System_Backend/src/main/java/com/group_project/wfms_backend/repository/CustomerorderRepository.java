package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Customer;
import com.group_project.wfms_backend.model.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerorderRepository  extends JpaRepository<CustomerOrder,Integer>
{
List<CustomerOrder> findByCustomerName(Integer customerId);


}
