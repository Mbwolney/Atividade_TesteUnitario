package br.com.pec.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {
    private Integer validityHours;
    private PecWebProperties pecWeb = new PecWebProperties();
    private String defaultLocal;

    @Getter
    @Setter
    public static class PecWebProperties {
        private String callbackUrl;
    }
}
