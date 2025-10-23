package br.com.pec.api.san.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "anuencia_request")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnuenciaRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String nsu; // identificador único do fluxo

    @Column(nullable = false)
    private String cpf; // formatado XXX.XXX.XXX-XX

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String localAssinatura; // preenchido automaticamente

    @Column(nullable = false)
    private String dataAssinatura; // DD/MM/AAAA

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // PENDENTE, CONCLUIDA, EXPIRADA, CANCELADA

    @Enumerated(EnumType.STRING)
    private Decision decision; // APROVADA, NEGADA, EXPIRADA, CANCELADA

    @Column
    private String respostaMensagem; // ex: "Cliente não concedeu anuência."

    @Column
    private String sanRespostaBruta; // JSON de resposta do SAN

    @Column(nullable = false)
    private Instant validadeAte; // controle de expiração

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public boolean isExpired(Instant now) {
        return now.isAfter(validadeAte);
    }

    public LocalDate getDataAssinaturaAsDate() {
        return LocalDate.parse(this.dataAssinatura, java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public Instant getDataAssinaturaInstant(ZoneId zoneId) {
        return this.getDataAssinaturaAsDate().atStartOfDay(zoneId).toInstant();
    }
}
