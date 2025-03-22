package com.pange.genfee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.pange.genfee")
public class GenfeeAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(GenfeeAdminApplication.class,args);
    }
}