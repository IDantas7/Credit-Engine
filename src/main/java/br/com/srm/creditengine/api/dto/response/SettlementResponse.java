package br.com.srm.creditengine.api.dto.response;

import java.time.LocalDateTime;

public record SettlementResponse(
        Long settlementId,
        Long simulationId,
        String idempotencyKey,
        LocalDateTime settledAt
) {
}
