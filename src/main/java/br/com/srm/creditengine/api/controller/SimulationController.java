package br.com.srm.creditengine.api.controller;

import br.com.srm.creditengine.api.dto.request.SimulationRequest;
import br.com.srm.creditengine.api.dto.response.SimulationResponse;
import br.com.srm.creditengine.application.result.SimulationResult;
import br.com.srm.creditengine.application.service.SimulationApplicationService;
import br.com.srm.creditengine.domain.model.Currency;
import br.com.srm.creditengine.domain.model.Receivable;
import br.com.srm.creditengine.domain.model.ReceivableType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/simulations")
@RequiredArgsConstructor
public class SimulationController {
    private final SimulationApplicationService simulationApplicationService;

    @PostMapping
    public ResponseEntity<SimulationResponse> simulate
            (@Valid @RequestBody SimulationRequest request) {
        Receivable receivable = new Receivable();
        receivable.setReceivableType(request.receivableType());
        receivable.setFaceValue(request.faceValue());
        receivable.setTermMonths(request.termMonths());
        receivable.setCurrency(request.currency());

        SimulationResult simulationResult = simulationApplicationService.simulate(receivable);
        SimulationResponse response = new SimulationResponse(
                simulationResult.simulationId(),
                simulationResult.receivableType(),
                simulationResult.faceValue(),
                simulationResult.termMonths(),
                simulationResult.paymentCurrency(),
                simulationResult.baseRate(),
                simulationResult.spread(),
                simulationResult.presentValue(),
                simulationResult.discount(),
                simulationResult.fxRate(),
                simulationResult.convertedValue(),
                simulationResult.createdAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
