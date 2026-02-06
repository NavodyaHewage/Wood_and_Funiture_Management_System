package com.group_project.wfms_backend.repository;

import com.group_project.wfms_backend.model.User;
import com.group_project.wfms_backend.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> findByRole(UserRole role);

    List<User> findByIsActiveTrue();

    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = :attempts WHERE u.username = :username")
    void updateFailedAttempts(@Param("attempts") int attempts, @Param("username") String username);

    @Modifying
    @Query("UPDATE User u SET u.accountLocked = :locked, u.lockTime = CURRENT_TIMESTAMP WHERE u.username = :username")
    void lockAccount(@Param("locked") boolean locked, @Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isActive = true")
    Optional<User> findActiveUserByUsername(@Param("username") String username);
}



