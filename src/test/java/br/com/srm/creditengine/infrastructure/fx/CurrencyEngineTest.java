package br.com.srm.creditengine.infrastructure.fx;

import br.com.srm.creditengine.domain.exception.ExchangeRateNotFoundException;
import br.com.srm.creditengine.domain.model.Currency;
import br.com.srm.creditengine.domain.model.ExchangeRate;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyEngineTest {

    @Test
    void shouldReturnLatestEffectiveExchangeRate() {
        CurrencyEngine currencyEngine = new CurrencyEngine();

        ExchangeRate rate13 = new ExchangeRate();
        rate13.setCurrency(Currency.USD);
        rate13.setEffectiveAt(LocalDateTime.of(2026, 9, 15, 13, 00));
        rate13.setRate(new BigDecimal("5.0002"));

        ExchangeRate rate14 = new ExchangeRate();
        rate14.setCurrency(Currency.USD);
        rate14.setEffectiveAt(LocalDateTime.of(2026, 9, 15, 14, 00));
        rate14.setRate(new BigDecimal("5.4321"));


        ExchangeRate rate15 = new ExchangeRate();
        rate15.setCurrency(Currency.USD);
        rate15.setEffectiveAt(LocalDateTime.of(2026, 9, 15, 15, 00));
        rate15.setRate(new BigDecimal("5.5543"));

        currencyEngine.addRate(rate13);
        currencyEngine.addRate(rate14);
        currencyEngine.addRate(rate15);

        LocalDateTime referenceTime = LocalDateTime.of(2026, 9, 15, 14, 30);

        BigDecimal expected =  new BigDecimal("5.4321");

        ExchangeRate actual = currencyEngine.getExchangeRate(Currency.USD, referenceTime);

        assertEquals(expected, actual.getRate());
    }

    @Test
    void shouldThrowExceptionWhenExchangeRateIsNotFound() {
        CurrencyEngine currencyEngine = new CurrencyEngine();

        ExchangeRate rate14 = new ExchangeRate();
        rate14.setCurrency(Currency.USD);
        rate14.setEffectiveAt(LocalDateTime.of(2026, 9, 15, 14, 00));
        rate14.setRate(new BigDecimal("5.4321"));

        currencyEngine.addRate(rate14);

        LocalDateTime referenceTime = LocalDateTime.of(2026, 9, 15, 13, 00);

        assertThrows(ExchangeRateNotFoundException.class, () -> currencyEngine.getExchangeRate(Currency.USD, referenceTime));
    }
}
