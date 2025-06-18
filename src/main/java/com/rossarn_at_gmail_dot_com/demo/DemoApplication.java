package com.rossarn_at_gmail_dot_com.demo;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {
    private static ConfigurableApplicationContext appContext;

    public static void main(String[] args) {
        appContext = SpringApplication.run(DemoApplication.class, args);
    }

}
