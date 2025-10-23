package br.com.pec.api.infrastructure.kafka;

import br.com.pec.api.config.KafkaTopicsProperties;
import br.com.pec.api.domain.model.AuthorizationRequest;
import br.com.pec.api.domain.model.Decision;
import br.com.pec.api.domain.repository.AuthorizationRequestRepository;
import br.com.pec.api.infrastructure.webhook.PecWebNotifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SanKafkaConsumer {

    private final KafkaTopicsProperties topicsProperties;
    private final ObjectMapper objectMapper;
    private final AuthorizationRequestRepository repository;
    private final PecWebNotifier notifier;

    @KafkaListener(topics = "#{kafkaTopicsProperties.reply}")
    public void onSanReply(@Payload String payload) {
        try {
            log.info("Received SAN reply: {}", payload);
            JsonNode root = objectMapper.readTree(payload);

            String businessKey = findText(root, "chaveNegocio", findText(root, "identificador", null));
            if (StringUtils.isBlank(businessKey)) {
                log.warn("Ignoring SAN reply without business key");
                return;
            }

            Optional<AuthorizationRequest> opt = repository.findByBusinessKey(businessKey);
            if (opt.isEmpty()) {
                log.warn("No AuthorizationRequest found for key={}", businessKey);
                return;
            }

            AuthorizationRequest entity = opt.get();
            entity.setSanResponseJson(payload);

            String nsu = findText(root, "nsu", findText(root, "nsuCanal", null));
            if (nsu != null) {
                entity.setNsu(nsu);
            }

            Decision decision = decideFromPayload(root, entity);
            entity.setDecision(decision);
            if (decision == Decision.APPROVED || decision == Decision.DENIED) {
                entity.setAcceptanceDate(OffsetDateTime.now());
            }

            repository.save(entity);

            notifier.notifyPecWeb(entity, decision == Decision.DENIED ? "Cliente não concedeu anuência." : null);
        } catch (Exception e) {
            log.error("Error processing SAN reply", e);
        }
    }

    private String findText(JsonNode node, String field, String defaultValue) {
        JsonNode f = node.path(field);
        if (!f.isMissingNode() && !f.isNull()) return f.asText();
        return defaultValue;
    }

    private Decision decideFromPayload(JsonNode root, AuthorizationRequest entity) {
        // If expired by time
        if (OffsetDateTime.now().isAfter(entity.getValidUntil())) {
            return Decision.EXPIRED;
        }
        // try to infer by common fields
        String status = StringUtils.defaultString(findText(root, "decisao", findText(root, "status", null))).toLowerCase();
        if (status.contains("aprov") || status.contains("approve") || status.equals("ok")) return Decision.APPROVED;
        if (status.contains("neg") || status.contains("reje")) return Decision.DENIED;
        if (status.contains("expir")) return Decision.EXPIRED;
        if (status.contains("cancel")) return Decision.CANCELED;
        // Default pending
        return Decision.PENDING;
    }
}
