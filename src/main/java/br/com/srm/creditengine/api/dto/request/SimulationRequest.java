package br.com.srm.creditengine.api.dto.request;

import br.com.srm.creditengine.domain.model.Currency;
import br.com.srm.creditengine.domain.model.ReceivableType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SimulationRequest(
        @NotNull(message = "O tipo de recebível é obrigatório")
        ReceivableType receivableType,

        @NotNull(message = "O valor de face é obrigatório")
        @Positive(message = "O valor de face deve ser maior que zero")
        BigDecimal faceValue,

        @NotNull(message = "O prazo é obrigatório")
        @Positive(message = "O prazo deve ser maior que zero")
        Integer termMonths,

        @NotNull(message = "A moeda de pagamento é obrigatória")
        Currency currency
) {
}
