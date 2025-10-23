package br.com.pec.api.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorizationCreateRequest {
    @NotBlank
    private String cpf;

    @NotBlank
    private String nome;

    // Opcional: dados para SAN
    private String numContaCorrente;

    // Número único gerado no front ou backend (se não vier, geramos)
    private String numeroUnico;
}
