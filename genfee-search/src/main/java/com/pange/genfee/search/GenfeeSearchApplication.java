package com.pange.genfee.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.pange.genfee")
public class GenfeeSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(GenfeeSearchApplication.class,args);
    }
}