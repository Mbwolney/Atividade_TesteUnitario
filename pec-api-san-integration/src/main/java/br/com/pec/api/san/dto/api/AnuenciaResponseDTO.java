package br.com.pec.api.san.dto.api;

import br.com.pec.api.san.domain.Decision;
import br.com.pec.api.san.domain.Status;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AnuenciaResponseDTO {
    String nsu;
    Status status;
    Decision decision;
    String mensagem;
}
