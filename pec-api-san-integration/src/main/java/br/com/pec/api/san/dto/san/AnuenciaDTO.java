package br.com.pec.api.san.dto.san;

import lombok.Data;

@Data
public class AnuenciaDTO {
    private String respostaCliente; // ex: APROVADA, NEGADA
    private String nomeProcesso;
    private String chaveNegocio;
    private String canal;
    private String nomeDispositivo;
    private String dataAnuencia; // DD/MM/AAAA
    private String horaAnuencia; // HH:mm:ss
    private String nsuCanal; // NSU
    private String idTransacao;
}
