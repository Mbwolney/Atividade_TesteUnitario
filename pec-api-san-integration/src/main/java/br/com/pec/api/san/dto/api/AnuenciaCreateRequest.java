package br.com.pec.api.san.dto.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AnuenciaCreateRequest {
    @NotBlank
    private String nomeCompleto;

    @NotBlank
    @Pattern(regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")
    private String cpf; // aceita formatado ou apenas números

    @NotBlank
    private String numContaCorrente;

    private String local; // opcional, preenche automaticamente se não enviado
}
