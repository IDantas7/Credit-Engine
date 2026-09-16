package br.com.srm.creditengine.application.result;

import br.com.srm.creditengine.domain.model.Currency;
import br.com.srm.creditengine.domain.model.ReceivableType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SimulationResult(
        Long simulationId,
        ReceivableType receivableType,
        BigDecimal faceValue,
        Integer termMonths,
        Currency paymentCurrency,
        BigDecimal baseRate,
        BigDecimal spread,
        BigDecimal presentValue,
        BigDecimal discount,
        BigDecimal fxRate,
        BigDecimal convertedValue,
        LocalDateTime createdAt
) {
}
