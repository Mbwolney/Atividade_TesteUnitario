package br.com.pec.api.san.repository;

import br.com.pec.api.san.domain.AnuenciaRequest;
import br.com.pec.api.san.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnuenciaRequestRepository extends JpaRepository<AnuenciaRequest, UUID> {
    Optional<AnuenciaRequest> findByNsu(String nsu);
    List<AnuenciaRequest> findByStatusAndValidadeAteBefore(Status status, Instant instant);
}
