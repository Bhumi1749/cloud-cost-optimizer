package com.cloudcost.optimizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CloudCostOptimizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudCostOptimizerApplication.class, args);
        System.out.println("✅ Cloud Cost Optimizer is running!");
        System.out.println("🌐 Open: http://localhost:8080");
    }
}