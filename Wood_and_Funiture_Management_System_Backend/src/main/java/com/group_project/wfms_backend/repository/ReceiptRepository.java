package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    boolean existsByReceiptNumber(String receiptNumber);

    List<Receipt> findByCustomer_CusId(Integer customerId);

    List<Receipt> findByDateBetween(LocalDate from, LocalDate to);

    @Query("SELECT r FROM Receipt r JOIN FETCH r.customer JOIN FETCH r.receiptDetails rd JOIN FETCH rd.customerOrderDetails WHERE r.receiptId = :id")
    Optional<Receipt> findByIdWithDetails(Long id);

    Optional<Receipt> findByReceiptNumber(String receiptNumber);
}
