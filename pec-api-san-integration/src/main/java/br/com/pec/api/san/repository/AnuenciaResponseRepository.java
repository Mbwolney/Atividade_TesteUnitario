package br.com.pec.api.san.repository;

import br.com.pec.api.san.domain.AnuenciaResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AnuenciaResponseRepository extends JpaRepository<AnuenciaResponse, UUID> {
    Optional<AnuenciaResponse> findByNsu(String nsu);
}
