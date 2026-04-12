package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    // Receipt Number එකෙන් සෙවීමට search method එකක් මෙතනට දාන්න
    Optional<Receipt> findByReceiptNumber(String receiptNumber);


}
