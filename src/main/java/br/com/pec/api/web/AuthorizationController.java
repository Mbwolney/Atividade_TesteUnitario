package br.com.pec.api.web;

import br.com.pec.api.application.AuthorizationOrchestrator;
import br.com.pec.api.domain.model.AuthorizationRequest;
import br.com.pec.api.domain.repository.AuthorizationRequestRepository;
import br.com.pec.api.web.dto.AuthorizationCreateRequest;
import br.com.pec.api.web.dto.AuthorizationStatusResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/anuencias")
@RequiredArgsConstructor
public class AuthorizationController {

    private final AuthorizationOrchestrator orchestrator;
    private final AuthorizationRequestRepository repository;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<AuthorizationStatusResponse> create(@Valid @RequestBody AuthorizationCreateRequest request) {
        AuthorizationRequest entity = orchestrator.createAndSend(request.getCpf(), request.getNome(), request.getNumContaCorrente(), request.getNumeroUnico());
        return ResponseEntity.ok(toResponse(entity, null));
    }

    @GetMapping("/{businessKey}")
    public ResponseEntity<AuthorizationStatusResponse> get(@PathVariable String businessKey) {
        AuthorizationRequest entity = repository.findByBusinessKey(businessKey)
                .orElseThrow(() -> new IllegalArgumentException("Requisição não encontrada"));
        return ResponseEntity.ok(toResponse(entity, null));
    }

    private AuthorizationStatusResponse toResponse(AuthorizationRequest entity, String message) {
        Map<String, Object> df = Map.of();
        try {
            if (entity.getDynamicFieldsJson() != null) {
                df = objectMapper.readValue(entity.getDynamicFieldsJson(), new TypeReference<>(){});
            }
        } catch (Exception ignored) {}

        return AuthorizationStatusResponse.builder()
                .businessKey(entity.getBusinessKey())
                .decision(entity.getDecision())
                .nsu(entity.getNsu())
                .message(message)
                .requestDate(entity.getRequestDate())
                .validUntil(entity.getValidUntil())
                .acceptanceDate(entity.getAcceptanceDate())
                .dynamicFields(df)
                .build();
    }
}
