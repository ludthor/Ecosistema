package com.ecosystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EcosystemAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcosystemAppApplication.class, args);
    }

}
