package br.com.srm.creditengine.api.controller;

import br.com.srm.creditengine.api.dto.request.ExchangeRateRequest;
import br.com.srm.creditengine.application.service.ExchangeRateApplicationService;
import br.com.srm.creditengine.domain.model.ExchangeRate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exchange-rates")
@RequiredArgsConstructor
public class ExchangeRateController {
    private final ExchangeRateApplicationService exchangeRateApplicationService;


    @PostMapping
    public ResponseEntity<Void> createExchangeRate(@Valid @RequestBody ExchangeRateRequest request) {
        exchangeRateApplicationService.addRate(
                request.currency(),
                request.rate(),
                request.effectiveAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }
}
