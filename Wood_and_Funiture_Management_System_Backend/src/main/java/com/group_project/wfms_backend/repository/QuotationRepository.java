package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.Quatation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface QuotationRepository  extends JpaRepository<Quatation,Integer>{



}
