package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.RawMaterialItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RawMaterialItemRepository extends JpaRepository<RawMaterialItem, Integer> {
}
