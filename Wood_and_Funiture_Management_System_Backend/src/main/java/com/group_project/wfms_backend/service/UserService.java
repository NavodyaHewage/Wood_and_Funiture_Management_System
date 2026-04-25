package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.MessageResponse;
import com.group_project.wfms_backend.model.User;
import com.group_project.wfms_backend.model.UserRole;
import com.group_project.wfms_backend.model.Employee;
import com.group_project.wfms_backend.model.Supplier;
import com.group_project.wfms_backend.model.Customer;
import com.group_project.wfms_backend.repository.CustomerRepository;
import com.group_project.wfms_backend.repository.EmployeeRepository;
import com.group_project.wfms_backend.repository.SupplierRepository;
import com.group_project.wfms_backend.repository.UserRepository;
import com.group_project.wfms_backend.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    private static final int MAX_FAILED_ATTEMPTS = 5;

    /**
     * Get current logged-in user details
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapUserToResponse(user);
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllUsers() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        Map<String, Object> response = mapUserToResponse(user);
        response.put("failedLoginAttempts", user.getFailedLoginAttempts());

        return response;
    }

    /**
     * Get users by role
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUsersByRole(UserRole role) {
        List<User> users = userRepository.findByRole(role);

        return users.stream()
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get active users
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActiveUsers() {
        List<User> users = userRepository.findByIsActiveTrue();

        return users.stream()
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update user
     */
    @Transactional
    public MessageResponse updateUser(Integer id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (updates.containsKey("email")) {
            String email = (String) updates.get("email");
            // Check if email is already used by another user
            if (email != null && !email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
                throw new RuntimeException("Email is already in use by another user");
            }
            user.setEmail(email);
        }

        if (updates.containsKey("nic")) {
            String nic = (String) updates.get("nic");
            String email = user.getEmail();
            if (email != null) {
                employeeRepository.findByEmail(email).ifPresent(e -> {
                    e.setNic(nic);
                    employeeRepository.save(e);
                });
                supplierRepository.findByEmail(email).ifPresent(s -> {
                    s.setNic(nic);
                    supplierRepository.save(s);
                });
                customerRepository.findByEmail(email).ifPresent(c -> {
                    c.setNic(nic);
                    customerRepository.save(c);
                });
            }
        }

        if (updates.containsKey("userDetails")) {
            user.setUserDetails((String) updates.get("userDetails"));
        }

        if (updates.containsKey("isActive")) {
            user.setIsActive((Boolean) updates.get("isActive"));
        }

        userRepository.save(user);

        log.info("User updated: {}", user.getUsername());

        return new MessageResponse("User updated successfully");
    }

    /**
     * Toggle user active status
     */
    @Transactional
    public MessageResponse toggleUserStatus(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setIsActive(!user.getIsActive());
        userRepository.save(user);

        String status = user.getIsActive() ? "activated" : "deactivated";
        log.info("User {} {}", user.getUsername(), status);

        return new MessageResponse("User " + status + " successfully");
    }

    /**
     * Unlock user account
     */
    @Transactional
    public MessageResponse unlockUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        resetFailedAttempts(user);

        log.info("User account unlocked: {}", user.getUsername());

        return new MessageResponse("User account unlocked successfully");
    }

    /**
     * Handle failed login attempt (public access)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = { BadCredentialsException.class,
            RuntimeException.class })
    public void handleFailedLogin(String username) {
        User user = userRepository.findByUsername(username)
                .orElse(null);

        if (user == null)
            return;

        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= MAX_FAILED_ATTEMPTS) {
            user.setAccountLocked(true);
            user.setLockTime(LocalDateTime.now());
            user.setIsActive(false); // is_active = 0
            userRepository.save(user);

            log.warn("Account locked and deactivated for user: {} due to {} failed attempts", username, attempts);

            throw new RuntimeException(
                    "Account locked and deactivated due to multiple failed login attempts. Please try again after 5 minutes.");
        }

        userRepository.save(user);

        int remainingAttempts = MAX_FAILED_ATTEMPTS - attempts;
        log.warn("Failed login attempt for user: {}. Remaining attempts: {}", username, remainingAttempts);

        throw new BadCredentialsException(
                "Invalid username or password. " + remainingAttempts + " attempts remaining.");
    }

    /**
     * Reset failed login attempts
     */
    @Transactional
    public void resetFailedAttempts(User user) {
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);
        user.setLockTime(null);
        userRepository.save(user);
    }

    /**
     * Change password using username and old password (public access)
     */
    @Transactional
    public MessageResponse changePassword(String username, String oldPassword, String newPassword) {
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("Username is required");
        }

        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password changed for user: {}", user.getUsername());

        return new MessageResponse("Password changed successfully");
    }

    /**
     * Reset password by admin
     */
    @Transactional
    public MessageResponse resetPassword(Integer userId, String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Password reset for user: {} by admin", user.getUsername());

        return new MessageResponse("Password reset successfully");
    }

    /**
     * Delete user
     */
    @Transactional
    public MessageResponse deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Prevent deleting the current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (user.getUserId().equals(userDetails.getId())) {
            throw new RuntimeException("You cannot delete your own account");
        }

        userRepository.delete(user);

        log.info("User deleted: {}", user.getUsername());

        return new MessageResponse("User deleted successfully");
    }

    /**
     * Check if username exists
     */
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if email exists
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get user statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findByIsActiveTrue().size();
        long adminCount = userRepository.findByRole(UserRole.ADMIN).size();
        long managerCount = userRepository.findByRole(UserRole.MANAGER).size();
        long supplierCount = userRepository.findByRole(UserRole.SUPPLIER).size();

        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("inactiveUsers", totalUsers - activeUsers);
        stats.put("adminCount", adminCount);
        stats.put("managerCount", managerCount);
        stats.put("supplierCount", supplierCount);

        return stats;
    }

    /**
     * Map User entity to response map
     */
    private Map<String, Object> mapUserToResponse(User user) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put("phoneNumber", user.getPhoneNumber());

        // Fetch NIC from linked entities
        String nic = null;
        if (user.getEmail() != null) {
            try {
                nic = employeeRepository.findByEmail(user.getEmail()).map(e -> e.getNic())
                        .orElseGet(() -> supplierRepository.findByEmail(user.getEmail()).map(s -> s.getNic())
                                .orElseGet(() -> customerRepository.findByEmail(user.getEmail()).map(c -> c.getNic())
                                        .orElse(null)));
            } catch (Exception e) {
                log.warn("Error fetching NIC for user {}: {}", user.getUsername(), e.getMessage());
            }
        }
        response.put("nic", nic);

        response.put("userDetails", user.getUserDetails());
        response.put("isActive", Boolean.TRUE.equals(user.getIsActive()));
        response.put("createdDate", user.getCreatedDate());
        response.put("lastLogin", user.getLastLogin());
        response.put("accountLocked", Boolean.TRUE.equals(user.getAccountLocked()));
        return response;
    }
}
