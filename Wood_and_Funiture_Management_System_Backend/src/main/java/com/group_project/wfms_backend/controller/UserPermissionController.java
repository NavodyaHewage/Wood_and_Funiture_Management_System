package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.MessageResponse;
import com.group_project.wfms_backend.model.User;
import com.group_project.wfms_backend.repository.UserRepository;
import com.group_project.wfms_backend.security.UserDetailsImpl;
import com.group_project.wfms_backend.service.UserPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@Slf4j
public class UserPermissionController {

    private final UserPermissionService userPermissionService;
    private final UserRepository userRepository;

    /**
     * Get all permissions for a user.
     * Admin only.
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserPermissions(@PathVariable Integer userId) {
        try {
            List<Map<String, Object>> permissions = userPermissionService.getUserPermissions(userId);
            return ResponseEntity.ok(permissions);
        } catch (Exception e) {
            log.error("Error retrieving permissions for user {}: ", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error retrieving user permissions: " + e.getMessage()));
        }
    }

    /**
     * Save/update permissions for a user.
     * Admin only.
     */
    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> saveUserPermissions(@PathVariable Integer userId, @RequestBody List<Map<String, Object>> permissions) {
        try {
            userPermissionService.saveUserPermissions(userId, permissions);
            return ResponseEntity.ok(new MessageResponse("Permissions updated successfully"));
        } catch (Exception e) {
            log.error("Error saving permissions for user {}: ", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error saving user permissions: " + e.getMessage()));
        }
    }

    /**
     * Get all allowed function permissions for the currently logged-in user.
     * Open to any authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyPermissions() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Unauthorized"));
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userRepository.findById(userDetails.getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<String> allowedFunctions = userPermissionService.getPermissionsForUser(user);
            return ResponseEntity.ok(allowedFunctions);
        } catch (Exception e) {
            log.error("Error retrieving my permissions: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error retrieving permissions: " + e.getMessage()));
        }
    }
}
