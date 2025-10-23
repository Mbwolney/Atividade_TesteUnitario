package br.com.pec.api.infrastructure.kafka;

import br.com.pec.api.config.KafkaTopicsProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SanKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicsProperties topicsProperties;
    private final ObjectMapper objectMapper;

    public void sendToSan(Object payload, String key) {
        try {
            String json = (payload instanceof String) ? (String) payload : objectMapper.writeValueAsString(payload);
            kafkaTemplate.send(topicsProperties.getRequest(), key, json);
            log.info("Sent message to SAN topic={}, key={}", topicsProperties.getRequest(), key);
        } catch (Exception e) {
            log.error("Failed to send message to SAN", e);
            throw new RuntimeException("Erro ao enviar mensagem ao SAN", e);
        }
    }
}
