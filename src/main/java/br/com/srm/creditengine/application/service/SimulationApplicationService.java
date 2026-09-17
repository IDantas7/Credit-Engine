package br.com.srm.creditengine.application.service;

import br.com.srm.creditengine.application.result.SimulationResult;
import br.com.srm.creditengine.domain.model.Currency;
import br.com.srm.creditengine.domain.model.ExchangeRate;
import br.com.srm.creditengine.domain.model.Receivable;
import br.com.srm.creditengine.domain.service.PricingCalculator;
import br.com.srm.creditengine.infrastructure.fx.CurrencyConverter;
import br.com.srm.creditengine.infrastructure.fx.CurrencyEngine;
import br.com.srm.creditengine.infrastructure.persistence.entity.SimulationEntity;
import br.com.srm.creditengine.infrastructure.persistence.repository.SimulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SimulationApplicationService {
    private final PricingCalculator pricingCalculator;
    private final SimulationRepository simulationRepository;
    private final CurrencyEngine currencyEngine;
    private final CurrencyConverter currencyConverter;

    @Transactional
    public SimulationResult simulate(Receivable receivable){

        BigDecimal presentValue = pricingCalculator.calculatePresentValue(receivable);
        BigDecimal discount = receivable.getFaceValue().subtract(presentValue);
        BigDecimal baseRate = pricingCalculator.getBaseRate();
        BigDecimal spread = pricingCalculator.getSpread(receivable.getReceivableType());

        BigDecimal fxRate = null;
        BigDecimal convertedValue = null;
        if (receivable.getCurrency() == Currency.USD){
            ExchangeRate exchangeRate = currencyEngine.getExchangeRate(
                    receivable.getCurrency(),
                    LocalDateTime.now());

            fxRate = exchangeRate.getRate();

            convertedValue = currencyConverter.convert(presentValue, exchangeRate);
        }

        SimulationEntity simulation = SimulationEntity.builder()
                .faceValue(receivable.getFaceValue())
                .receivableType(receivable.getReceivableType())
                .termMonths(receivable.getTermMonths())
                .paymentCurrency(receivable.getCurrency())
                .presentValue(presentValue)
                .discount(discount)
                .baseRate(baseRate)
                .spread(spread)
                .fxRate(fxRate)
                .convertedValue(convertedValue)
                .build();

        SimulationEntity savedSimulation = simulationRepository.saveAndFlush(simulation);
        return new SimulationResult(
                savedSimulation.getId(),
                savedSimulation.getReceivableType(),
                savedSimulation.getFaceValue(),
                savedSimulation.getTermMonths(),
                savedSimulation.getPaymentCurrency(),
                savedSimulation.getBaseRate(),
                savedSimulation.getSpread(),
                savedSimulation.getPresentValue(),
                savedSimulation.getDiscount(),
                savedSimulation.getFxRate(),
                savedSimulation.getConvertedValue(),
                savedSimulation.getCreatedAt()
        );
    }

}
