package br.com.pec.api.application;

import br.com.pec.api.config.AppProperties;
import br.com.pec.api.config.KafkaTopicsProperties;
import br.com.pec.api.domain.model.AuthorizationRequest;
import br.com.pec.api.domain.model.Decision;
import br.com.pec.api.domain.repository.AuthorizationRequestRepository;
import br.com.pec.api.infrastructure.kafka.SanKafkaProducer;
import br.com.pec.api.util.CpfUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationOrchestrator {

    private static final DateTimeFormatter DATE_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AuthorizationRequestRepository repository;
    private final SanKafkaProducer producer;
    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;
    private final KafkaTopicsProperties topicsProperties;

    @Transactional
    public AuthorizationRequest createAndSend(String cpfRaw, String nome, String numContaCorrente, String numeroUnicoOpt) {
        String cpfFormatted = CpfUtils.format(cpfRaw);
        String local = appProperties.getDefaultLocal();
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime validUntil = now.plusHours(appProperties.getValidityHours());

        String numeroUnico = numeroUnicoOpt != null ? numeroUnicoOpt : UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String businessKey = "MCR-" + numeroUnico;

        Map<String, Object> dynamicFields = Map.of(
                "nome", nome,
                "cpf", cpfFormatted,
                "local", local,
                "data", DATE_BR.format(now)
        );

        String sanRequestJson = buildSanRequestJson(numeroUnico, nome, cpfFormatted, numContaCorrente, validUntil, dynamicFields);

        AuthorizationRequest entity = AuthorizationRequest.builder()
                .businessKey(businessKey)
                .customerName(nome)
                .customerCpf(cpfFormatted)
                .local(local)
                .requestDate(now)
                .validUntil(validUntil)
                .decision(Decision.PENDING)
                .sanRequestJson(sanRequestJson)
                .dynamicFieldsJson(toJson(dynamicFields))
                .build();

        repository.save(entity);

        producer.sendToSan(sanRequestJson, businessKey);
        log.info("SAN request sent for businessKey={}", businessKey);

        return entity;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildSanRequestJson(String numeroUnico, String nome, String cpfFormatado, String numContaCorrente, OffsetDateTime validUntil, Map<String, Object> dynamicFields) {
        Map<String, Object> root = new HashMap<>();
        String chave = "MCR-" + numeroUnico;
        root.put("identificador", chave);
        root.put("nomeProcesso", "AdquirirAnuenciaSimples");
        root.put("origem", "MCR");
        root.put("tituloAnuencia", "Solicitação Cartão");
        root.put("chaveNegocio", chave);
        root.put("cpfCnpjCliente", cpfFormatado.replaceAll("\\D", ""));
        root.put("cpfs", null);
        root.put("numContaCorrente", numContaCorrente);
        root.put("validade", validUntil.atZoneSameInstant(ZoneId.of("America/Sao_Paulo")).toOffsetDateTime().toString());
        root.put("possuiTermo", false);
        root.put("possuiSeguro", false);

        // variaveis
        Map<String, Object> bloco = new HashMap<>();
        bloco.put("titulo", "Dados de cédula");
        List<Map<String, Object>> dados = List.of(
                dado("Nome", nome, "TEXTO"),
                dado("CPF", cpfFormatado, "TEXTO"),
                dado("Local", String.valueOf(dynamicFields.get("local")), "TEXTO"),
                dado("Data", String.valueOf(dynamicFields.get("data")), "DATA")
        );
        bloco.put("dados", dados);
        root.put("variaveis", List.of(bloco));

        Map<String, Object> variavelLista = new HashMap<>();
        variavelLista.put("titulo", "Solicitacao CARTÃO");
        variavelLista.put("chaveNegocio", chave);
        variavelLista.put("nomeProcesso", "AdquirirAnuenciaSimples");
        variavelLista.put("listaVariaveis", List.of(
                dado("Vencimento", DATE_BR.format(validUntil), "DATA"),
                dado("Objeto", "Visa Platinum", "TEXTO"),
                dado("Origem", "MCR", "TEXTO")
        ));
        root.put("variavelLista", variavelLista);

        Map<String, Object> anuencia = new HashMap<>();
        anuencia.put("respostaCliente", null);
        anuencia.put("nomeProcesso", null);
        anuencia.put("chaveNegocio", null);
        anuencia.put("canal", null);
        anuencia.put("nomeDispositivo", null);
        anuencia.put("dataAnuencia", null);
        anuencia.put("horaAnuencia", null);
        anuencia.put("nsuCanal", null);
        anuencia.put("idTransacao", null);
        root.put("anuencia", anuencia);

        return toJson(root);
    }

    private Map<String, Object> dado(String titulo, String valor, String tipo) {
        Map<String, Object> map = new HashMap<>();
        map.put("titulo", titulo);
        map.put("valor", valor);
        map.put("tipo", tipo);
        map.put("valorSemFormatacao", null);
        return map;
    }
}
