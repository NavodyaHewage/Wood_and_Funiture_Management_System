package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, java.lang.Integer> {
    List<UserPermission> findByUserUserId(Integer userId);
    Optional<UserPermission> findByUserUserIdAndFunctionNameIgnoreCase(Integer userId, String functionName);
    void deleteByUserUserId(Integer userId);
}
