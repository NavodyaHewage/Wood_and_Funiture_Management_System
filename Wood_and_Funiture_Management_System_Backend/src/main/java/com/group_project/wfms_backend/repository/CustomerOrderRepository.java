

package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.CustomerOrder;
import com.group_project.wfms_backend.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {

    List<CustomerOrder> findByCustomer_CusId(Integer customerId);

    List<CustomerOrder> findByStatus(OrderStatus status);

    List<CustomerOrder> findByOrderDateBetween(LocalDate from, LocalDate to);

    @Query("SELECT o FROM CustomerOrder o JOIN FETCH o.customer JOIN FETCH o.orderDetails d JOIN FETCH d.productCategory WHERE o.orderId = :id")
    java.util.Optional<CustomerOrder> findByIdWithDetails(Long id);

    @Query(value = "CALL Generate_Order_Number()", nativeQuery = true)
    String generateOrderNumber();
}