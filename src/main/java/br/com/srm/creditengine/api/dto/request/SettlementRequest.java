package br.com.srm.creditengine.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SettlementRequest(
        @NotNull(message = "O id não pode ser nulo")
        @Positive(message = "O id da simulação deve ser maior que zero")
        Long simulationId,

        @NotBlank(message = "A chave de idempotência não pode ser vazia")
        String idempotencyKey
) {
}
