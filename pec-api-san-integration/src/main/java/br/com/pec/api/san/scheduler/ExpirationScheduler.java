package br.com.pec.api.san.scheduler;

import br.com.pec.api.san.domain.Decision;
import br.com.pec.api.san.domain.Status;
import br.com.pec.api.san.repository.AnuenciaRequestRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class ExpirationScheduler {

    private final AnuenciaRequestRepository repository;

    public ExpirationScheduler(AnuenciaRequestRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedDelayString = "PT1M") // a cada 1 minuto
    @Transactional
    public void expirePendentes() {
        var pendentes = repository.findByStatusAndValidadeAteBefore(Status.PENDENTE, Instant.now());
        pendentes.forEach(req -> {
            req.setStatus(Status.EXPIRADA);
            req.setDecision(Decision.EXPIRADA);
        });
        repository.saveAll(pendentes);
    }
}
