package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.controller.CustomerOrderDetails;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerOrderDetailsRepository extends JpaRepository<CustomerOrderDetails, Integer> {



    // 🔹 Get all items of an order
    @Query("SELECT d FROM CustomerOrderDetails d WHERE d.order.orderId = :orderId")
    List<CustomerOrderDetails> findByOrderId(@Param("orderId") Integer orderId);

    // 🔹 Delete all items of an order (if updating order)
    @Modifying
    @Query("DELETE FROM CustomerOrderDetails d WHERE d.order.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") Integer orderId);

    // 🔹 Get total quantity of a product sold
    @Query("SELECT SUM(d.quantity) FROM CustomerOrderDetails d WHERE d.productCategory.id = :productId")
    BigDecimal getTotalQuantitySold(@Param("productId") Integer productId);






}
