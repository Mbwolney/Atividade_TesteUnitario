package br.com.pec.api.san.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "anuencia_response")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnuenciaResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nsu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Decision decision;

    @Lob
    @Column
    private String sanPayload; // payload completo de retorno

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
