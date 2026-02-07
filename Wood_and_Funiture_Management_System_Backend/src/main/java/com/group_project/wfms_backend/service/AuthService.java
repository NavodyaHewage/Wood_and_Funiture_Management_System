package com.group_project.wfms_backend.service;

import com.group_project.wfms_backend.dto.auth.*;
import com.group_project.wfms_backend.model.*;
import com.group_project.wfms_backend.repository.*;
import com.group_project.wfms_backend.security.UserDetailsImpl;
import com.group_project.wfms_backend.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final EmployeeRepository employeeRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;

    /**
     * Authenticate user and generate JWT tokens
     */
    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        // Check if user exists
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        // Check if account is locked
        if (!user.isAccountNonLocked()) {
            throw new RuntimeException(
                    "Account is locked due to multiple failed login attempts. Please try again after 5 minutes.");
        }

        // Check if account is active
        if (!user.getIsActive()) {
            throw new RuntimeException("Account is deactivated. Please contact administrator.");
        }

        try {
            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate tokens
            String jwt = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(loginRequest.getUsername());

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // Reset failed attempts on successful login
            if (user.getFailedLoginAttempts() > 0) {
                userService.resetFailedAttempts(user);
            }

            // Update last login and activity
            user.setLastLogin(LocalDateTime.now());
            user.setLastActivity(LocalDateTime.now());
            userRepository.save(user);

            log.info("User {} logged in successfully", loginRequest.getUsername());

            // Calculate token expiration time
            long expiresIn = jwtUtils.getRemainingTimeInMs(jwt);

            return new JwtResponse(
                    jwt,
                    refreshToken,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getRole(),
                    expiresIn);

        } catch (BadCredentialsException e) {
            userService.handleFailedLogin(loginRequest.getUsername());
        }
        return null; // Should not reach here
    }

    /**
     * Register new user
     */
    @Transactional
    public MessageResponse signup(SignupRequest signUpRequest) {
        if (Boolean.TRUE.equals(signUpRequest.getIsSystemUser())) {
            return registerSystemUser(signUpRequest);
        } else {
            return registerNonSystemUser(signUpRequest);
        }
    }

    private MessageResponse registerSystemUser(SignupRequest signUpRequest) {
        // Validation for credentials
        if (signUpRequest.getUsername() == null || signUpRequest.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required for system users!");
        }
        if (signUpRequest.getPassword() == null || signUpRequest.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required for system users!");
        }

        // Check if username exists
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        // Check if email exists
        if (signUpRequest.getEmail() != null && userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setEmail(signUpRequest.getEmail());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        user.setUserDetails(signUpRequest.getUserDetails());
        user.setRole(signUpRequest.getRole() != null ? signUpRequest.getRole() : UserRole.MANAGER);
        user.setIsActive(true);
        user.setFailedLoginAttempts(0);
        user.setAccountLocked(false);

        userRepository.save(user);

        // Role-specific persistence
        if (user.getRole() == UserRole.MANAGER) {
            Employee employee = new Employee();
            employee.setFullName(signUpRequest.getFullName());
            employee.setNic(signUpRequest.getNic());
            employee.setAddress(signUpRequest.getAddress());
            employee.setMobileNumber(signUpRequest.getMobile());
            employee.setEmail(signUpRequest.getEmail());
            employee.setDesignation("Manager");
            if (signUpRequest.getDateJoined() != null && !signUpRequest.getDateJoined().isEmpty()) {
                employee.setDateJoined(LocalDate.parse(signUpRequest.getDateJoined()));
            }
            employeeRepository.save(employee);
        } else if (user.getRole() == UserRole.SUPPLIER) {
            if (!"Regular".equalsIgnoreCase(signUpRequest.getSupCat())) {
                throw new RuntimeException("Only Regular suppliers can be system users!");
            }
            Supplier supplier = new Supplier();
            supplier.setSupName(signUpRequest.getFullName());
            supplier.setSupCat(signUpRequest.getSupCat());
            supplier.setMobile(signUpRequest.getMobile());
            supplier.setAddress(signUpRequest.getAddress());
            supplier.setEmail(signUpRequest.getEmail());
            supplierRepository.save(supplier);
        }

        log.info("New system user registered: {}", user.getUsername());
        return new MessageResponse("System user registered successfully!");
    }

    private MessageResponse registerNonSystemUser(SignupRequest signUpRequest) {
        String entityType = signUpRequest.getEntityType();
        if (entityType == null) {
            throw new RuntimeException("Entity type is required for non-system users!");
        }

        switch (entityType.toUpperCase()) {
            case "EMPLOYEE":
                Employee employee = new Employee();
                employee.setFullName(signUpRequest.getFullName());
                employee.setNic(signUpRequest.getNic());
                employee.setAddress(signUpRequest.getAddress());
                employee.setMobileNumber(signUpRequest.getMobile());
                employee.setEmail(signUpRequest.getEmail());
                employee.setDesignation(signUpRequest.getDesignation());
                if (signUpRequest.getDateJoined() != null && !signUpRequest.getDateJoined().isEmpty()) {
                    employee.setDateJoined(LocalDate.parse(signUpRequest.getDateJoined()));
                }
                employeeRepository.save(employee);
                break;

            case "SUPPLIER":
                if ("Regular".equalsIgnoreCase(signUpRequest.getSupCat())) {
                    throw new RuntimeException("Regular suppliers must be system users!");
                }
                Supplier supplier = new Supplier();
                supplier.setSupName(signUpRequest.getFullName());
                supplier.setSupCat(signUpRequest.getSupCat());
                supplier.setMobile(signUpRequest.getMobile());
                supplier.setAddress(signUpRequest.getAddress());
                supplier.setEmail(signUpRequest.getEmail());
                supplierRepository.save(supplier);
                break;

            case "CUSTOMER":
                Customer customer = new Customer();
                customer.setCusName(signUpRequest.getFullName());
                customer.setMobile(signUpRequest.getMobile());
                customer.setNic(signUpRequest.getNic());
                customer.setAddress(signUpRequest.getAddress());
                customer.setEmail(signUpRequest.getEmail());
                customerRepository.save(customer);
                break;

            default:
                throw new RuntimeException("Invalid entity type: " + entityType);
        }

        log.info("New non-system {} registered: {}", entityType, signUpRequest.getFullName());
        return new MessageResponse("Non-system user registered successfully!");
    }

    /**
     * Refresh access token
     */
    @Transactional(readOnly = true)
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtUtils.validateJwtToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        // Get username from refresh token
        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);

        // Check if user still exists and is active
        User user = userRepository.findActiveUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found or inactive"));

        // Generate new access token
        String newAccessToken = jwtUtils.generateTokenFromUsername(username);
        long expiresIn = jwtUtils.getRemainingTimeInMs(newAccessToken);

        // Update last activity on successful refresh
        user.setLastActivity(LocalDateTime.now());
        userRepository.save(user);

        log.info("Token refreshed for user: {}", username);

        return JwtResponse.builder()
                .token(newAccessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .expiresIn(expiresIn)
                .build();
    }

    /**
     * Validate JWT token
     */
    public MessageResponse validateToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);

            if (jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);
                long remainingTime = jwtUtils.getRemainingTimeInMs(jwt);

                return new MessageResponse(
                        "Token is valid for user: " + username + ". Expires in " + (remainingTime / 1000) + " seconds");
            }
        }

        throw new RuntimeException("Invalid token");
    }

    /**
     * Logout user
     */
    public MessageResponse logout() {
        SecurityContextHolder.clearContext();
        return new MessageResponse("Logout successful");
    }

}
