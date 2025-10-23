package br.com.pec.api.infrastructure.webhook;

import br.com.pec.api.config.AppProperties;
import br.com.pec.api.domain.model.AuthorizationRequest;
import br.com.pec.api.domain.model.Decision;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PecWebNotifier {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    public void notifyPecWeb(AuthorizationRequest entity, String overrideMessage) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("businessKey", entity.getBusinessKey());
            payload.put("nsu", entity.getNsu());
            payload.put("decision", entity.getDecision().name());
            payload.put("message", overrideMessage);
            payload.put("dynamicFields", parseDynamicFields(entity.getDynamicFieldsJson()));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(appProperties.getPecWeb().getCallbackUrl(), request, Void.class);
            log.info("Notified PEC-WEB callbackUrl={}", appProperties.getPecWeb().getCallbackUrl());
        } catch (Exception e) {
            log.error("Failed to notify PEC-WEB", e);
        }
    }

    private Map<String, Object> parseDynamicFields(String json) {
        try {
            if (json == null) return Map.of();
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }
}
