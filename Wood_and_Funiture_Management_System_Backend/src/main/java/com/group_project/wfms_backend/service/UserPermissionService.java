package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.model.User;
import com.group_project.wfms_backend.model.UserPermission;
import com.group_project.wfms_backend.model.UserRole;
import com.group_project.wfms_backend.repository.UserPermissionRepository;
import com.group_project.wfms_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPermissionService {

    private final UserPermissionRepository userPermissionRepository;
    private final UserRepository userRepository;

    // Master list of all functional modules in the system
    public static final List<String> ALL_FUNCTIONS = Arrays.asList(
            "employee-management",
            "attendance-management",
            "loan-management",
            "payroll-management",
            "designation-salary",
            "supplier-management",
            "supply-request-management",
            "log-management",
            "raw-material-cutting",
            "customer-management",
            "quotation-management",
            "order-management",
            "receipts",
            "expenses",
            "product-category",
            "stock-inventory",
            "accounts-dashboard"
    );

    /**
     * Get permission mappings for a specific user.
     * Integrates database state with the master function list.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getUserPermissions(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        List<UserPermission> dbPermissions = userPermissionRepository.findByUserUserId(userId);
        Map<String, Boolean> permissionMap = dbPermissions.stream()
                .collect(Collectors.toMap(
                        p -> p.getFunctionName().toLowerCase(),
                        UserPermission::getCanAccess,
                        (v1, v2) -> v1
                ));

        List<Map<String, Object>> result = new ArrayList<>();
        for (String func : ALL_FUNCTIONS) {
            Map<String, Object> map = new HashMap<>();
            map.put("functionName", func);
            
            // Employees default to TRUE if not yet explicitly set, to preserve existing access.
            // Other roles are also handled gracefully.
            boolean canAccess = permissionMap.getOrDefault(func.toLowerCase(), user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.EMPLOYEE);
            map.put("canAccess", canAccess);
            result.add(map);
        }

        return result;
    }

    /**
     * Get permission function names that the currently logged-in user is permitted to access.
     */
    @Transactional(readOnly = true)
    public List<String> getPermissionsForUser(User user) {
        if (user.getRole() == UserRole.ADMIN) {
            return ALL_FUNCTIONS;
        }

        List<UserPermission> dbPermissions = userPermissionRepository.findByUserUserId(user.getUserId());
        Map<String, Boolean> permissionMap = dbPermissions.stream()
                .collect(Collectors.toMap(
                        p -> p.getFunctionName().toLowerCase(),
                        UserPermission::getCanAccess,
                        (v1, v2) -> v1
                ));

        List<String> allowed = new ArrayList<>();
        for (String func : ALL_FUNCTIONS) {
            boolean canAccess = permissionMap.getOrDefault(func.toLowerCase(), user.getRole() == UserRole.EMPLOYEE);
            if (canAccess) {
                allowed.add(func);
            }
        }
        return allowed;
    }

    /**
     * Save/Update permission configurations for a specific user.
     */
    @Transactional
    public void saveUserPermissions(Integer userId, List<Map<String, Object>> permissions) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Delete existing ones
        userPermissionRepository.deleteByUserUserId(userId);

        // Save new permissions
        for (Map<String, Object> perm : permissions) {
            String functionName = (String) perm.get("functionName");
            Boolean canAccess = (Boolean) perm.get("canAccess");

            if (functionName != null && canAccess != null) {
                UserPermission userPermission = UserPermission.builder()
                        .user(user)
                        .functionName(functionName.toLowerCase())
                        .canAccess(canAccess)
                        .build();
                userPermissionRepository.save(userPermission);
            }
        }
        log.info("Successfully updated permissions for user: {}", user.getUsername());
    }
}
