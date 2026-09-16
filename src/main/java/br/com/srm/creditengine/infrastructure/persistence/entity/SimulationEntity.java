package br.com.srm.creditengine.infrastructure.persistence.entity;

import br.com.srm.creditengine.domain.model.Currency;
import br.com.srm.creditengine.domain.model.ReceivableType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_simulation")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SimulationEntity {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "simulation_seq")
    @SequenceGenerator(
            name = "simulation_seq",
            sequenceName = "simulation_seq",
            allocationSize = 1)
    @Column(name = "simulation_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "receivable_type", nullable = false)
    private ReceivableType receivableType;

    @Column(name = "face_value", precision = 19, scale = 2, nullable = false)
    private BigDecimal faceValue;

    @Column(name = "term_months", nullable = false)
    private Integer termMonths;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_currency", nullable = false)
    private Currency paymentCurrency;

    @Column(name = "base_rate", precision = 10, scale = 6, nullable = false)
    private BigDecimal baseRate;

    @Column(name = "spread", precision = 10, scale = 6, nullable = false)
    private BigDecimal spread;

    @Column(name = "fx_rate", precision = 10, scale = 6)
    private BigDecimal fxRate;

    @Column(name = "present_value", precision = 19, scale = 2, nullable = false)
    private BigDecimal presentValue;

    @Column(name = "discount", precision = 19, scale = 2, nullable = false)
    private BigDecimal discount;

    @Column(name = "converted_value", precision = 19, scale = 2)
    private BigDecimal convertedValue;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
