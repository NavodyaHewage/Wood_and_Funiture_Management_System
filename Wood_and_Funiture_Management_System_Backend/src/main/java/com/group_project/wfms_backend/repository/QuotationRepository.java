package com.group_project.wfms_backend.repository;





import com.group_project.wfms_backend.model.Quotation;
import com.group_project.wfms_backend.model.QuotationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QuotationRepository extends JpaRepository<Quotation, Integer> {

    List<Quotation> findByCustomer_CusId(Integer customerId);

    List<Quotation> findByStatus(QuotationStatus status);

    List<Quotation> findByQuotationDateBetween(LocalDate startDate, LocalDate endDate);

    List<Quotation> findByCustomer_CusIdAndStatus(Integer customerId, QuotationStatus status);

    @Query("SELECT q FROM Quotation q WHERE q.validUntil < :today AND q.status = :status")
    List<Quotation> findExpiredQuotations(@Param("today") LocalDate today, @Param("status") QuotationStatus status);

    @Query("SELECT q FROM Quotation q WHERE q.status = :status AND q.validUntil BETWEEN :today AND :soon")
    List<Quotation> findExpiringSoon(@Param("status") QuotationStatus status, @Param("today") LocalDate today, @Param("soon") LocalDate soon);

    @Query("SELECT q FROM Quotation q ORDER BY q.quotationDate DESC")
    List<Quotation> findAllOrderByDateDesc();
}