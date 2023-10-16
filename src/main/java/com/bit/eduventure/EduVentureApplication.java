package com.bit.eduventure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EduVentureApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduVentureApplication.class, args);
    }

}
