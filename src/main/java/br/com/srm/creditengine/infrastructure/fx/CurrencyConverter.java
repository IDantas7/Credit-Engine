package br.com.srm.creditengine.infrastructure.fx;

import br.com.srm.creditengine.domain.model.ExchangeRate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Component
public class CurrencyConverter {
    public BigDecimal convert(BigDecimal amountInBrl, ExchangeRate exchangeRate) {
        BigDecimal amountInUsd = amountInBrl.divide(exchangeRate.getRate(), MathContext.DECIMAL128);
        return amountInUsd.setScale(2, RoundingMode.HALF_EVEN);
    }
}
