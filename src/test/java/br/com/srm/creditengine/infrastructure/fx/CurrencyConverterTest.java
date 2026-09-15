package br.com.srm.creditengine.infrastructure.fx;

import br.com.srm.creditengine.domain.model.Currency;
import br.com.srm.creditengine.domain.model.ExchangeRate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyConverterTest {

    @Test
    public void shouldCurrencyConverter() {

        CurrencyConverter converter = new CurrencyConverter();

        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.setCurrency(Currency.USD);
        exchangeRate.setRate(new BigDecimal("5.4321"));
        exchangeRate.setEffectiveAt(LocalDateTime.of(2026, 9, 15, 15, 30));

        BigDecimal amountInBrl = new BigDecimal("92859.94");

        BigDecimal expected = new BigDecimal("17094.67");

        BigDecimal actual = converter.convert(amountInBrl, exchangeRate);

        assertEquals(expected, actual);

    }
}
