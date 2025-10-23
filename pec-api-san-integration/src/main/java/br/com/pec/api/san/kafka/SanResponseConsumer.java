package br.com.pec.api.san.kafka;

import br.com.pec.api.san.service.AnuenciaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class SanResponseConsumer {

    private final AnuenciaService anuenciaService;

    public SanResponseConsumer(AnuenciaService anuenciaService) {
        this.anuenciaService = anuenciaService;
    }

    @KafkaListener(topics = "${pec.kafka.topics.san-envio-anuencia-mcr}", groupId = "pec-api-san-grp")
    public void onMessage(ConsumerRecord<String, Object> record, @Payload Object payload, @Header(name = "kafka_receivedTimestamp", required = false) Long ts) {
        // Delegar o processamento para o serviço de domínio
        anuenciaService.processarRetornoSan(record.key(), payload);
    }
}
