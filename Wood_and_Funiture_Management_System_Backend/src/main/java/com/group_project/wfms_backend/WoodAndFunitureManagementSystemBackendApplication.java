package com.group_project.wfms_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class WoodAndFunitureManagementSystemBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(WoodAndFunitureManagementSystemBackendApplication.class, args);
    }

}
