package com.group_project.wfms_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import jakarta.annotation.PostConstruct;

@Configuration
public class DatabaseMigrationConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        try {
            // Attempt to add the missing Balance column if it does not exist
            jdbcTemplate.execute("ALTER TABLE Employee_loan ADD COLUMN Balance DECIMAL(15,2) GENERATED ALWAYS AS (Loan_Amount - Total_Deducted) STORED");
            System.out.println("Successfully added 'Balance' column to Employee_loan table.");
        } catch (Exception e) {
            // If it fails, it likely already exists or there's another issue, just log and ignore
            System.out.println("'Balance' column already exists or could not be added: " + e.getMessage());
        }
    }
}
