package com.group_project.wfms_backend.controller;

import com.group_project.wfms_backend.dto.auth.*;
import com.group_project.wfms_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {


    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.error("Bad credentials for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(e.getMessage()));

        } catch (LockedException e) {
            log.error("Account locked: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.LOCKED)
                    .body(new MessageResponse(e.getMessage()));

        } catch (DisabledException e) {
            log.error("Account disabled: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse(e.getMessage()));

        } catch (RuntimeException e) {
            log.error("Login error: {}", e.getMessage());

            // Check if it's a lockout message
            if (e.getMessage().contains("locked")) {
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body(new MessageResponse(e.getMessage()));
            }

            // Check if it's a deactivated account
            if (e.getMessage().contains("deactivated")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new MessageResponse(e.getMessage()));
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected authentication error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Authentication failed"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            MessageResponse response = authService.signup(signUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            log.error("Registration error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected registration error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error: Could not register user!"));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            JwtResponse response = authService.refreshToken(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Token refresh error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected token refresh error: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Could not refresh token"));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            MessageResponse response = authService.validateToken(token);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Token validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected validation error: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Token validation failed"));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        try {
            MessageResponse response = authService.logout();
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Logout error: ", e);
            return ResponseEntity.ok(new MessageResponse("Logout completed"));
        }
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            if (username == null || username.isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Username is required"));
            }
            MessageResponse response = authService.forgotPassword(username);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Forgot password error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected forgot password error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error resetting password"));
        }
    }
}



