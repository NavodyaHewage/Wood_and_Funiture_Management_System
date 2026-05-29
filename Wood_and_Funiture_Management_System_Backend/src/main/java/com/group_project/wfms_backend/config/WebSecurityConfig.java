package com.group_project.wfms_backend.config;

import com.group_project.wfms_backend.security.UserDetailsServiceImpl;
import com.group_project.wfms_backend.security.jwt.AuthEntryPointJwt;
import com.group_project.wfms_backend.security.jwt.AuthTokenFilter;
import com.group_project.wfms_backend.security.EmployeePermissionFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final AuthEntryPointJwt unauthorizedHandler;
    private final AuthTokenFilter authTokenFilter;
    private final EmployeePermissionFilter employeePermissionFilter;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/users/change-password").permitAll()
                        .requestMatchers("/users/me").permitAll()
                        .requestMatchers("/attendance/**").permitAll()
                        .requestMatchers("/test/**").permitAll()

                        // Admin only endpoints
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/users/**", "/users").hasRole("ADMIN")
                        .requestMatchers("/v1/supply-raw-materials/my-supplies").hasRole("SUPPLIER")

                        // Management endpoints - Admin and Employee
                        .requestMatchers(
                                "/employees/**",
                                "/suppliers/**",
                                "/customers/**",
                                "/payroll/**",
                                "/assets/**",
                                "/orders/**",
                                "/v1/cutting-fees/**",
                                "/v1/grn/**",
                                "/designation-salary/**",
                                "/loans/**",
                                "/paysheet-details/**",
                                "/salary-payments/**",
                                "/equity/**",
                                "/expenses/**",
                                "/income-account/**",
                                "/loan-rules/**",
                                "/payroll-records/**",
                                "/product-categories/**",
                                "/profit/**",
                                "/quotations/**",
                                "/v1/raw-material-items/**",
                                "/receipts/**",
                                "/v1/supply-raw-materials/**",
                                "/v1/raw-material-cutting/**"
                        ).hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers("/supply-requests/**").hasAnyRole("ADMIN", "EMPLOYEE", "SUPPLIER")

                        // Any other request must be authenticated
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(employeePermissionFilter, AuthTokenFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
