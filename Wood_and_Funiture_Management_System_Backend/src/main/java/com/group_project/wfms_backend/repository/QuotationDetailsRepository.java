package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.QuotationDeatails;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuotationDetailsRepository extends JpaRepository<QuotationDeatails,Integer> {

    Optional<QuotationDeatails>FindFirstByUser_Username(@Param("username") String username);

}