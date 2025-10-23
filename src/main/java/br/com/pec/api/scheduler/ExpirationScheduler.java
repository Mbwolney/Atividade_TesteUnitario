package br.com.pec.api.scheduler;

import br.com.pec.api.domain.model.AuthorizationRequest;
import br.com.pec.api.domain.model.Decision;
import br.com.pec.api.domain.repository.AuthorizationRequestRepository;
import br.com.pec.api.infrastructure.webhook.PecWebNotifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpirationScheduler {

    private final AuthorizationRequestRepository repository;
    private final PecWebNotifier notifier;

    @Scheduled(fixedDelayString = "PT1M") // every 1 minute
    public void expirePendingRequests() {
        List<AuthorizationRequest> toExpire = repository.findByDecisionAndValidUntilBefore(Decision.PENDING, OffsetDateTime.now());
        for (AuthorizationRequest entity : toExpire) {
            entity.setDecision(Decision.EXPIRED);
            repository.save(entity);
            notifier.notifyPecWeb(entity, null);
            log.info("Expired authorization businessKey={}", entity.getBusinessKey());
        }
    }
}
