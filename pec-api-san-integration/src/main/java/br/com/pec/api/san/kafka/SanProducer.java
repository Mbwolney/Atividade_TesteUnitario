package br.com.pec.api.san.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SanProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${pec.kafka.topics.san-processo-simples}")
    private String topicSanProcessoSimples;

    public SanProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendToSan(String key, Object payload) {
        kafkaTemplate.send(topicSanProcessoSimples, key, payload);
    }
}
