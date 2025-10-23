package br.com.pec.api.san.dto.san;

import lombok.Data;

import java.util.List;

@Data
public class SanSolicitacaoAnuenciaDTO {
    private String identificador; // Ex: MCR-{numeroUnico}
    private String nomeProcesso; // "AdquirirAnuenciaSimples"
    private String origem; // "MCR"
    private String tituloAnuencia; // "Solicitação Cartão"
    private String chaveNegocio; // Ex: MCR-{numeroUnico}
    private String cpfCnpjCliente; // CPF sem formatação
    private List<String> cpfs; // null
    private String numContaCorrente;
    private String validade; // Prazo de expiração (opcional, mas o MCR vai controlar)
    private boolean possuiTermo; // true se for usar o HTML no variavelLista
    private boolean possuiSeguro; // false
    private List<VariavelTituloDTO> variaveis; // Lista de Dados e Crédito
    private VariavelListaDTO variavelLista; // Título da Solicitação CARTÃO
    private AnuenciaDTO anuencia;
}
