package br.com.srm.creditengine.domain.service;

import br.com.srm.creditengine.domain.exception.PricingStrategyNotFoundException;
import br.com.srm.creditengine.domain.model.Receivable;
import br.com.srm.creditengine.domain.model.ReceivableType;
import br.com.srm.creditengine.domain.strategy.PricingStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PricingCalculator {
    private final BigDecimal baseRate;

    private Map<ReceivableType, PricingStrategy> strategies = new HashMap<>();

    public PricingCalculator(List<PricingStrategy> strategies, @Value("${pricing.base-rate}")BigDecimal baseRate) {
        for (PricingStrategy strategy : strategies) {
            this.strategies.put(strategy.getReceivableType(), strategy);
        }
        this.baseRate = baseRate;
    }

    public BigDecimal calculatePresentValue(Receivable receivable){
        PricingStrategy strategy = strategies.get(receivable.getReceivableType());

        if(strategy == null){
            throw new PricingStrategyNotFoundException("Strategy not found for receivable: " + receivable.getReceivableType());
        }

        return strategy.calculatePresentValue(
                receivable.getFaceValue(),
                receivable.getTermMonths(),
                baseRate
        );

    }
}
