package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Receipt;
import com.group_project.wfms_backend.model.ReceiptDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiptDetailsRepository extends JpaRepository<ReceiptDetails,Integer> {

    List<ReceiptDetails> findByReceipt_ReceiptId(Long receiptId);




}
