package br.com.pec.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PecApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PecApiApplication.class, args);
    }
}
