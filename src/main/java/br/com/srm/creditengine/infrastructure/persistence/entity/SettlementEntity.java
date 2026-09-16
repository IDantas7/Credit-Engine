package br.com.srm.creditengine.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_settlement")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SettlementEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "settlement_seq")
    @SequenceGenerator(
            name = "settlement_seq",
            sequenceName = "settlement_seq",
            allocationSize = 1)
    @Column(name = "settlement_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "simulation_id", nullable = false, unique = true, updatable = false)
    private SimulationEntity simulation;

    @Column(name = "idempotency_key", nullable = false, unique = true, updatable = false)
    private String idempotencyKey;

    @CreationTimestamp
    @Column(name = "settled_at", nullable = false, updatable = false)
    private LocalDateTime settledAt;
}
