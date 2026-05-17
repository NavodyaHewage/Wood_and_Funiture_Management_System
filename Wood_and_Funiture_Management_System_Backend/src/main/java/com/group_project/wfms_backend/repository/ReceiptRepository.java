package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    boolean existsByReceiptNumber(String receiptNumber);

    List<Receipt> findByCustomer_CusId(Integer customerId);

    List<Receipt> findByDateBetween(LocalDate from, LocalDate to);

    @Query("SELECT r FROM Receipt r JOIN FETCH r.customer JOIN FETCH r.receiptDetails rd JOIN FETCH rd.customerOrderDetails WHERE r.receiptId = :id")
    Optional<Receipt> findByIdWithDetails(@Param("id") Long id);

    Optional<Receipt> findByReceiptNumber(String receiptNumber);

    @Query("SELECT DISTINCT r FROM Receipt r LEFT JOIN FETCH r.customer c " +
           "LEFT JOIN r.receiptDetails rd " +
           "LEFT JOIN rd.customerOrderDetails cod " +
           "LEFT JOIN cod.order o " +
           "WHERE (:search IS NULL OR :search = '' " +
           "OR LOWER(r.receiptNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.cusName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Receipt> searchReceipts(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(r) FROM Receipt r WHERE r.date = :date")
    long countByDate(@Param("date") LocalDate date);
}
