package com.tarun.PrintJobsSpring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PrintJobsSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrintJobsSpringApplication.class, args);
    }
}
