package br.com.pec.api.san.dto.san;

import lombok.Data;

import java.util.List;

@Data
public class VariavelListaDTO {
    private String titulo;
    private String chaveNegocio;
    private String nomeProcesso;
    private java.util.List<ListaVariavelDTO> listaVariaveis;

    @Data
    public static class ListaVariavelDTO {
        private String titulo;
        private String valor;
        private String tipo;
        private String valorSemFormatacao;
    }
}
