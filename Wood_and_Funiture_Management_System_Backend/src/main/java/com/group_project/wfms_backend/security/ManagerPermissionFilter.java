package com.group_project.wfms_backend.security;

import com.group_project.wfms_backend.model.User;
import com.group_project.wfms_backend.model.UserPermission;
import com.group_project.wfms_backend.model.UserRole;
import com.group_project.wfms_backend.repository.UserPermissionRepository;
import com.group_project.wfms_backend.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class ManagerPermissionFilter extends OncePerRequestFilter {

    private final UserPermissionRepository userPermissionRepository;
    private final UserRepository userRepository;

    // Maps API endpoint prefixes to their standard function identifiers
    private static final Map<String, String> PATH_TO_FUNCTION_MAP = new HashMap<>();

    static {
        PATH_TO_FUNCTION_MAP.put("/employees", "employee-management");
        PATH_TO_FUNCTION_MAP.put("/suppliers", "supplier-management");
        PATH_TO_FUNCTION_MAP.put("/customers", "customer-management");
        PATH_TO_FUNCTION_MAP.put("/attendance", "attendance-management");
        PATH_TO_FUNCTION_MAP.put("/loans", "loan-management");
        PATH_TO_FUNCTION_MAP.put("/loan-rules", "loan-management");
        PATH_TO_FUNCTION_MAP.put("/payroll", "payroll-management");
        PATH_TO_FUNCTION_MAP.put("/paysheet-details", "payroll-management");
        PATH_TO_FUNCTION_MAP.put("/salary-payments", "payroll-management");
        PATH_TO_FUNCTION_MAP.put("/payroll-records", "payroll-management");
        PATH_TO_FUNCTION_MAP.put("/designation-salary", "designation-salary");
        PATH_TO_FUNCTION_MAP.put("/v1/raw-material-items", "log-management");
        PATH_TO_FUNCTION_MAP.put("/v1/supply-raw-materials", "log-management");
        PATH_TO_FUNCTION_MAP.put("/v1/raw-material-cutting", "log-management");
        PATH_TO_FUNCTION_MAP.put("/v1/cutting-fees", "log-management");
        PATH_TO_FUNCTION_MAP.put("/v1/grn", "log-management");
        PATH_TO_FUNCTION_MAP.put("/supply-requests", "supply-request-management");
        PATH_TO_FUNCTION_MAP.put("/receipts", "receipts");
        PATH_TO_FUNCTION_MAP.put("/expenses", "expenses");
        PATH_TO_FUNCTION_MAP.put("/orders", "order-management");
        PATH_TO_FUNCTION_MAP.put("/quotations", "quotation-management");
        PATH_TO_FUNCTION_MAP.put("/product-categories", "product-category");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            boolean isManager = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));

            if (isManager) {
                String requestUri = request.getRequestURI();
                String contextPath = request.getContextPath();
                
                // Strip the context path (e.g. /api)
                if (contextPath != null && !contextPath.isEmpty() && requestUri.startsWith(contextPath)) {
                    requestUri = requestUri.substring(contextPath.length());
                }

                // Match with target functions
                String matchedFunction = null;
                for (Map.Entry<String, String> entry : PATH_TO_FUNCTION_MAP.entrySet()) {
                    if (requestUri.startsWith(entry.getKey())) {
                        matchedFunction = entry.getValue();
                        break;
                    }
                }

                if (matchedFunction != null) {
                    String username = authentication.getName();
                    User user = userRepository.findByUsername(username).orElse(null);

                    if (user != null) {
                        final String functionName = matchedFunction;
                        List<UserPermission> dbPermissions = userPermissionRepository.findByUserUserId(user.getUserId());
                        
                        // Check if permission is explicitly set to false. 
                        // Managers default to TRUE if no database record exists yet.
                        boolean hasPermission = dbPermissions.stream()
                                .filter(p -> p.getFunctionName().equalsIgnoreCase(functionName))
                                .map(UserPermission::getCanAccess)
                                .findFirst()
                                .orElse(true);

                        if (!hasPermission) {
                            log.warn("Access Denied: Manager {} tried to access unauthorized endpoint {} (Function: {})",
                                    username, request.getRequestURI(), matchedFunction);
                            
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");
                            response.getWriter().write("{\"message\":\"Access Denied: You do not have permission to access " + matchedFunction + "\"}");
                            return;
                        }
                    }
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
