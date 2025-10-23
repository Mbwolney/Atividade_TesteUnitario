package br.com.pec.api.san.dto.san;

import lombok.Data;

import java.util.List;

@Data
public class VariavelTituloDTO {
    private String titulo;
    private List<VariavelDadoDTO> dados;
}
