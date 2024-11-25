package com.willamsc.personalwebsite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PersonalWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalWebsiteApplication.class, args);
    }
}
