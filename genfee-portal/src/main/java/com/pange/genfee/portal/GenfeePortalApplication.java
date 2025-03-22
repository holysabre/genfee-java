package com.pange.genfee.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.pange.genfee")
public class GenfeePortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenfeePortalApplication.class, args);
    }

}
