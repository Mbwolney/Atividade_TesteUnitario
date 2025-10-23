package br.com.pec.api.san;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PecSanIntegrationApplication {
    public static void main(String[] args) {
        SpringApplication.run(PecSanIntegrationApplication.class, args);
    }
}
