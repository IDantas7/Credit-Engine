package br.com.srm.creditengine.domain.strategy;

import br.com.srm.creditengine.domain.model.ReceivableType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class DuplicataPricingStrategy implements PricingStrategy{
    private static final BigDecimal SPREAD = new BigDecimal("0.015");

    @Override
    public ReceivableType getReceivableType(){
        return ReceivableType.DUPLICATA_MERCANTIL;
    }

    @Override
    public BigDecimal calculatePresentValue(BigDecimal faceValue, int termMonths, BigDecimal baseRate) {
        BigDecimal rate = BigDecimal.ONE.add(baseRate).add(SPREAD);

        BigDecimal denominator = rate.pow(termMonths);

        BigDecimal presentValue = faceValue.divide(denominator, MathContext.DECIMAL128);

        return presentValue.setScale(2, RoundingMode.HALF_EVEN);

    }
}
