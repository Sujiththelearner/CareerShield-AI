package com.careershield;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * CareerShield AI - AI-Based Fake Internship and Job Posting Detection System
 * Main Spring Boot Application Entry Point.
 */
@SpringBootApplication
public class CareerShieldApplication {

    public static void main(String[] args) {
        SpringApplication.run(CareerShieldApplication.class, args);
        System.out.println("==========================================================");
        System.out.println("🛡️  CareerShield AI Backend is Running on http://localhost:8080");
        System.out.println("    Tagline: 'Verify Before You Apply.'");
        System.out.println("==========================================================");
    }
}
