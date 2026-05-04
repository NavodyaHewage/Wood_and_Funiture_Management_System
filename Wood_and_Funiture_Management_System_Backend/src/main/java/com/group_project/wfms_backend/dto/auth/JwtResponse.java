package com.group_project.wfms_backend.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {

    private String token;
    private String refreshToken;
    @Builder.Default
    private String type = "Bearer";
    private Integer userId;
    private String username;
    private String email;
    private String role;
    private Long expiresIn; // in milliseconds
    private Boolean passwordResetRequired;

    public JwtResponse(String token, String refreshToken, Integer userId, String username, String email, String role,
            Long expiresIn, Boolean passwordResetRequired) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.expiresIn = expiresIn;
        this.passwordResetRequired = passwordResetRequired;
    }
}
