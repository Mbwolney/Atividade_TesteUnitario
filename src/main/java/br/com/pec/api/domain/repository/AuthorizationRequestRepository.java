package br.com.pec.api.domain.repository;

import br.com.pec.api.domain.model.AuthorizationRequest;
import br.com.pec.api.domain.model.Decision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface AuthorizationRequestRepository extends JpaRepository<AuthorizationRequest, Long> {
    Optional<AuthorizationRequest> findByBusinessKey(String businessKey);

    List<AuthorizationRequest> findByDecisionAndValidUntilBefore(Decision decision, OffsetDateTime before);
}
