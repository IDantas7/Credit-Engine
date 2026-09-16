package br.com.srm.creditengine.domain.strategy;

import br.com.srm.creditengine.domain.model.ReceivableType;

import java.math.BigDecimal;

public interface PricingStrategy {

    ReceivableType getReceivableType();

    BigDecimal getSpread();

    BigDecimal calculatePresentValue(
            BigDecimal faceValue,
            int termMonths,
            BigDecimal baseRate
    );
}
