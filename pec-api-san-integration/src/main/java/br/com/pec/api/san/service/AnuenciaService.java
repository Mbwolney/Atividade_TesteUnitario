package br.com.pec.api.san.service;

import br.com.pec.api.san.domain.AnuenciaRequest;
import br.com.pec.api.san.domain.AnuenciaResponse;
import br.com.pec.api.san.domain.Decision;
import br.com.pec.api.san.domain.Status;
import br.com.pec.api.san.dto.api.AnuenciaCreateRequest;
import br.com.pec.api.san.dto.api.AnuenciaResponseDTO;
import br.com.pec.api.san.dto.san.*;
import br.com.pec.api.san.kafka.SanProducer;
import br.com.pec.api.san.repository.AnuenciaRequestRepository;
import br.com.pec.api.san.repository.AnuenciaResponseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AnuenciaService {

    private static final DateTimeFormatter DATE_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AnuenciaRequestRepository requestRepository;
    private final AnuenciaResponseRepository responseRepository;
    private final SanProducer sanProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${pec.anuencia.validade-default-hours:24}")
    private long validadeDefaultHours;

    public AnuenciaService(AnuenciaRequestRepository requestRepository,
                           AnuenciaResponseRepository responseRepository,
                           SanProducer sanProducer) {
        this.requestRepository = requestRepository;
        this.responseRepository = responseRepository;
        this.sanProducer = sanProducer;
    }

    @Transactional
    public AnuenciaResponseDTO criarEEnviarSolicitacao(AnuenciaCreateRequest req) {
        if (!CpfUtils.isValid(req.getCpf())) {
            throw new IllegalArgumentException("CPF inválido");
        }
        String cpfFormatado = CpfUtils.format(req.getCpf());
        String local = (req.getLocal() == null || req.getLocal().isBlank()) ? "BRASILIA/DF" : req.getLocal().trim();
        String data = LocalDate.now(ZoneId.of("America/Sao_Paulo")).format(DATE_BR);

        String nsu = "MCR-" + UUID.randomUUID();
        Instant validade = Instant.now().plusSeconds(validadeDefaultHours * 3600);

        AnuenciaRequest entity = AnuenciaRequest.builder()
                .nsu(nsu)
                .cpf(cpfFormatado)
                .nome(req.getNomeCompleto())
                .localAssinatura(local)
                .dataAssinatura(data)
                .status(Status.PENDENTE)
                .validadeAte(validade)
                .build();
        requestRepository.save(entity);

        SanSolicitacaoAnuenciaDTO payload = buildSanPayload(entity);
        sanProducer.sendToSan(nsu, payload);

        return AnuenciaResponseDTO.builder()
                .nsu(nsu)
                .status(entity.getStatus())
                .decision(entity.getDecision())
                .mensagem("Solicitação enviada ao SAN. Aguarde o retorno.")
                .build();
    }

    private SanSolicitacaoAnuenciaDTO buildSanPayload(AnuenciaRequest entity) {
        SanSolicitacaoAnuenciaDTO dto = new SanSolicitacaoAnuenciaDTO();
        dto.setIdentificador(entity.getNsu());
        dto.setNomeProcesso("AdquirirAnuenciaSimples");
        dto.setOrigem("MCR");
        dto.setTituloAnuencia("Solicitação Cartão");
        dto.setChaveNegocio(entity.getNsu());
        dto.setCpfCnpjCliente(CpfUtils.digits(entity.getCpf()));
        dto.setCpfs(null);
        dto.setNumContaCorrente("");
        dto.setValidade(null); // controle feito pelo MCR/PEC-API
        dto.setPossuiTermo(false);
        dto.setPossuiSeguro(false);

        List<VariavelTituloDTO> variaveis = new ArrayList<>();
        VariavelTituloDTO dadosCedula = new VariavelTituloDTO();
        dadosCedula.setTitulo("Dados2 de cédula");
        List<VariavelDadoDTO> dados = new ArrayList<>();
        dados.add(buildDado("Nome", entity.getNome(), "TEXTO"));
        dados.add(buildDado("CPF", entity.getCpf(), "TEXTO"));
        dados.add(buildDado("Local", entity.getLocalAssinatura(), "TEXTO"));
        dados.add(buildDado("Data", entity.getDataAssinatura(), "DATA"));
        dadosCedula.setDados(dados);
        variaveis.add(dadosCedula);
        dto.setVariaveis(variaveis);

        VariavelListaDTO lista = new VariavelListaDTO();
        lista.setTitulo("Solicitacao CARTÃO");
        lista.setChaveNegocio(entity.getNsu());
        lista.setNomeProcesso("AdquirirAnuenciaSimples");
        List<VariavelListaDTO.ListaVariavelDTO> listaVars = new ArrayList<>();
        VariavelListaDTO.ListaVariavelDTO venc = new VariavelListaDTO.ListaVariavelDTO();
        venc.setTitulo("Vencimento");
        venc.setValor(LocalDate.now(ZoneId.of("America/Sao_Paulo")).plusDays(1).format(DATE_BR) + " às 23:59");
        venc.setTipo("DATA");
        listaVars.add(venc);
        lista.setListaVariaveis(listaVars);
        dto.setVariavelLista(lista);

        AnuenciaDTO anuencia = new AnuenciaDTO();
        dto.setAnuencia(anuencia);
        return dto;
    }

    private VariavelDadoDTO buildDado(String titulo, String valor, String tipo) {
        VariavelDadoDTO v = new VariavelDadoDTO();
        v.setTitulo(titulo);
        v.setValor(valor);
        v.setTipo(tipo);
        v.setValorSemFormatacao(null);
        return v;
    }

    @Transactional
    public void processarRetornoSan(String key, Object payload) {
        // Espera-se que o payload contenha nsu e decisão. Persistimos e atualizamos request
        String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            json = String.valueOf(payload);
        }

        String nsu = key != null ? key : extractNsuFromPayload(payload);
        if (nsu == null) return;

        AnuenciaRequest req = requestRepository.findByNsu(nsu).orElse(null);
        if (req == null) return;
        if (req.isExpired(Instant.now())) {
            req.setStatus(Status.EXPIRADA);
            req.setDecision(Decision.EXPIRADA);
            req.setSanRespostaBruta(json);
            requestRepository.save(req);
            responseRepository.save(AnuenciaResponse.builder()
                    .nsu(nsu)
                    .decision(Decision.EXPIRADA)
                    .sanPayload(json)
                    .build());
            return;
        }

        Decision decision = mapDecisionFromPayload(payload);
        req.setStatus(Status.CONCLUIDA);
        req.setDecision(decision);
        if (decision == Decision.NEGADA) {
            req.setRespostaMensagem("Cliente não concedeu anuência.");
        }
        req.setSanRespostaBruta(json);
        requestRepository.save(req);
        responseRepository.save(AnuenciaResponse.builder()
                .nsu(nsu)
                .decision(decision)
                .sanPayload(json)
                .build());
    }

    private String extractNsuFromPayload(Object payload) {
        try {
            // Tenta mapear generico: procura campos usuais
            var node = objectMapper.valueToTree(payload);
            if (node.has("nsu")) return node.get("nsu").asText();
            if (node.has("chaveNegocio")) return node.get("chaveNegocio").asText();
            if (node.has("identificador")) return node.get("identificador").asText();
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private Decision mapDecisionFromPayload(Object payload) {
        try {
            var node = objectMapper.valueToTree(payload);
            String valor = null;
            if (node.has("decisao")) valor = node.get("decisao").asText();
            else if (node.has("resposta")) valor = node.get("resposta").asText();
            else if (node.has("anuencia") && node.get("anuencia").has("respostaCliente")) {
                valor = node.get("anuencia").get("respostaCliente").asText();
            }
            if (valor == null) return Decision.APROVADA; // default otimista
            valor = valor.trim().toUpperCase();
            return switch (valor) {
                case "APROVADA", "APROVADO", "ACEITE", "SIM" -> Decision.APROVADA;
                case "NEGADA", "NEGADO", "NAO", "NÃO", "REJEITADA", "REJEITOU" -> Decision.NEGADA;
                case "CANCELADA", "CANCELADO" -> Decision.CANCELADA;
                case "EXPIRADA", "EXPIRADO" -> Decision.EXPIRADA;
                default -> Decision.NEGADA;
            };
        } catch (Exception e) {
            return Decision.NEGADA;
        }
    }

    @Transactional(readOnly = true)
    public AnuenciaResponseDTO consultar(String nsu) {
        AnuenciaRequest req = requestRepository.findByNsu(nsu).orElse(null);
        if (req == null) return null;
        return AnuenciaResponseDTO.builder()
                .nsu(req.getNsu())
                .status(req.getStatus())
                .decision(req.getDecision())
                .mensagem(req.getRespostaMensagem())
                .build();
    }
}
