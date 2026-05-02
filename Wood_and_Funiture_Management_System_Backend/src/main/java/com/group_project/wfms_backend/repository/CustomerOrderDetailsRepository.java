package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.CustomerOrderDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CustomerOrderDetailsRepository extends JpaRepository<CustomerOrderDetails, Long> {



    // 🔹 Get all items of an order
    @Query("SELECT d FROM CustomerOrderDetails d WHERE d.order.orderId = :orderId")
    List<CustomerOrderDetails> findByOrderId(@Param("orderId") Long orderId);

    // 🔹 Delete all items of an order (if updating order)
    @Modifying
    @Query("DELETE FROM CustomerOrderDetails d WHERE d.order.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);

    // 🔹 Get total quantity of a product sold
    @Query("SELECT SUM(d.quantity) FROM CustomerOrderDetails d WHERE d.productCategory.productCatId = :productId")
    BigDecimal getTotalQuantitySold(@Param("productId") Integer productId);


}
