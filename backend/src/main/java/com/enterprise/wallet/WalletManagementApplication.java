package com.enterprise.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class WalletManagementApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(WalletManagementApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("Wallet Management System Started Successfully!");
        System.out.println("API Documentation: http://localhost:8080/swagger-ui.html");
        System.out.println("========================================\n");
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
