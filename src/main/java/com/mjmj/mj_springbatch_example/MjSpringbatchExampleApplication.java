package com.mjmj.mj_springbatch_example;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableBatchProcessing
@SpringBootApplication
public class MjSpringbatchExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MjSpringbatchExampleApplication.class, args);
    }

}
