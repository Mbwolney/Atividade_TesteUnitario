package br.com.pec.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.topics")
@Getter
@Setter
public class KafkaTopicsProperties {
    private String request;
    private String reply;
}
