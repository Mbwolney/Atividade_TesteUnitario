package br.com.pec.api.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "authorization_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorizationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "business_key", nullable = false, unique = true, length = 80)
    private String businessKey; // e.g. MCR-<numeroUnico>

    @Column(name = "customer_name", nullable = false, length = 200)
    private String customerName;

    @Column(name = "customer_cpf", nullable = false, length = 14)
    private String customerCpf; // formatted xxx.xxx.xxx-xx

    @Column(name = "local_value", nullable = false, length = 120)
    private String local;

    @Column(name = "request_date", nullable = false)
    private OffsetDateTime requestDate;

    @Column(name = "valid_until", nullable = false)
    private OffsetDateTime validUntil;

    @Column(name = "acceptance_date")
    private OffsetDateTime acceptanceDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false, length = 20)
    private Decision decision;

    @Column(name = "nsu", length = 100)
    private String nsu;

    @Lob
    @Column(name = "dynamic_fields_json")
    private String dynamicFieldsJson;

    @Lob
    @Column(name = "san_request_json")
    private String sanRequestJson;

    @Lob
    @Column(name = "san_response_json")
    private String sanResponseJson;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onPersist() {
        this.updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
