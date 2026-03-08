package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.QuotationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuotationDetailsRepository extends JpaRepository<QuotationDetails, Integer> {

    List<QuotationDetails> findByQuotation_QuotationId(Integer quotationId);

    void deleteByQuotation_QuotationId(Integer quotationId);

    //@Query("SELECT qd FROM QuotationDetails qd WHERE qd.productCategory.productCatId = :catId")
    List<QuotationDetails> findByProductCategoryId(Integer productCatId);
}