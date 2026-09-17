package br.com.srm.creditengine.api.controller;

import br.com.srm.creditengine.api.dto.request.SettlementRequest;
import br.com.srm.creditengine.api.dto.response.SettlementResponse;
import br.com.srm.creditengine.application.result.SettlementResult;
import br.com.srm.creditengine.application.service.SettlementApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/settlements")
@RequiredArgsConstructor
public class SettlementController {
    private final SettlementApplicationService settlementApplicationService;

    @PostMapping
    public ResponseEntity<SettlementResponse> createSettlement(@Valid @RequestBody SettlementRequest request) {
        SettlementResult settlementResult =
                settlementApplicationService.settle(request.simulationId(), request.idempotencyKey());
        SettlementResponse response = new SettlementResponse(
                settlementResult.settlementId(),
                settlementResult.simulationId(),
                settlementResult.idempotencyKey(),
                settlementResult.settledAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SettlementResponse>> findAllSettlements() {
        List<SettlementResponse> responses  = settlementApplicationService.findAll().stream()
                .map(settlementResult -> new SettlementResponse(
                        settlementResult.settlementId(),
                        settlementResult.simulationId(),
                        settlementResult.idempotencyKey(),
                        settlementResult.settledAt()
                ))
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(responses);

    }
}
