package br.com.srm.creditengine.application.result;

import java.time.LocalDateTime;

public record SettlementResult(
        Long settlementId,
        Long simulationId,
        String idempotencyKey,
        LocalDateTime settledAt
) {
}
