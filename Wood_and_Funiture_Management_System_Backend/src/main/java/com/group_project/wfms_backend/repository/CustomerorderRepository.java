package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.CustomerOrder;
import com.group_project.wfms_backend.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerorderRepository  extends JpaRepository<CustomerOrder,Integer> {

    Optional<CustomerOrder> findByOrderid(Integer Orderid);
    boolean existsByOrderid(Integer OrderId);

    List<CustomerOrder> findOrderByCustomerid(Integer customerid);
    List<CustomerOrder> findByOrderStatus(Integer orderid);

    @Query("SELECT o FROM CustomerOrder o WHERE o.customer.id = :customerId")
    List<CustomerOrder> findOrdersByCustomerId(@Param("customerId") Integer customerId);



    // 🔹 Get orders by status
    @Query("SELECT o FROM CustomerOrder o WHERE o.status = :status")
    List<CustomerOrder> findByStatus(@Param("status") OrderStatus status);


    @Query("SELECT o FROM CustomerOrder o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<CustomerOrder> findOrdersBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);





    // 🔹 Get today orders
    @Query("SELECT o FROM CustomerOrder o WHERE o.orderDate = CURRENT_DATE")
    List<CustomerOrder> findTodayOrders();

    // 🔹 Get pending orders of a customer
    @Query("SELECT o FROM CustomerOrder o WHERE o.customer.id = :customerId AND o.status = 'Pending'")
    List<CustomerOrder> findPendingOrdersByCustomer(@Param("customerId") Integer customerId);




    // 🔹 Get total sales amount (All)
    @Query("SELECT SUM(o.totalAmount) FROM CustomerOrder o WHERE o.status = 'Completed'")
    BigDecimal getTotalCompletedSales();


}

