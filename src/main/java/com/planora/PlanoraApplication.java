package com.planora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PlanoraApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlanoraApplication.class, args);
    }
}
