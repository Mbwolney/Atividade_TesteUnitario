package br.com.pec.api.web.dto;

import br.com.pec.api.domain.model.Decision;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.Map;

@Value
@Builder
public class AuthorizationStatusResponse {
    String businessKey;
    Decision decision;
    String nsu;
    String message;
    OffsetDateTime requestDate;
    OffsetDateTime validUntil;
    OffsetDateTime acceptanceDate;
    Map<String, Object> dynamicFields;
}
