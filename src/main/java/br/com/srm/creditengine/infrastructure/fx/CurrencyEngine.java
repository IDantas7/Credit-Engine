package br.com.srm.creditengine.infrastructure.fx;

import br.com.srm.creditengine.domain.exception.ExchangeRateNotFoundException;
import br.com.srm.creditengine.domain.model.ExchangeRate;
import br.com.srm.creditengine.domain.model.Currency;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class CurrencyEngine {

    private final List<ExchangeRate> exchangeRates = new ArrayList<>();

    public void addRate(ExchangeRate exchangeRate) {
        exchangeRates.add(exchangeRate);
    }

    public ExchangeRate getExchangeRate(Currency currency, LocalDateTime referenceTime) {
        return exchangeRates.stream()
                .filter(c -> c.getCurrency().equals(currency))
                .filter(e -> e.getEffectiveAt().isBefore(referenceTime)||e.getEffectiveAt().isEqual(referenceTime))
                .max(Comparator.comparing(ExchangeRate::getEffectiveAt))
                .orElseThrow(() -> new ExchangeRateNotFoundException("Exchange rate not found for currency: " + currency));

    }
}
