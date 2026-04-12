package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.EquityAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquityAccountRepository extends JpaRepository<EquityAccount,Integer> {


}
