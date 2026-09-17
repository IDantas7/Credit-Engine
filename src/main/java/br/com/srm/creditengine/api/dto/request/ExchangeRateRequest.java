package br.com.srm.creditengine.api.dto.request;

import br.com.srm.creditengine.domain.model.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExchangeRateRequest(
        @NotNull
        Currency currency,

        @NotNull
        @Positive
        BigDecimal rate,

        @NotNull
        LocalDateTime effectiveAt
) {
}
