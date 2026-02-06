package com.group_project.wfms_backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WoodAndFunitureManagementSystemBackendApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void testJwtSecret() {
        String jwtSecret = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
        try {
            byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(jwtSecret);
            System.out.println("Key bytes length: " + keyBytes.length);
            io.jsonwebtoken.security.Keys.hmacShaKeyFor(keyBytes);
            System.out.println("Secret is valid");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JWT Secret check failed", e);
        }
    }

}
