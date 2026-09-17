package br.com.srm.creditengine.application.service;

import br.com.srm.creditengine.domain.model.Currency;
import br.com.srm.creditengine.domain.model.ExchangeRate;
import br.com.srm.creditengine.infrastructure.fx.CurrencyEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ExchangeRateApplicationService {
    private final CurrencyEngine currencyEngine;

    public void addRate(
            Currency currency,
            BigDecimal rate,
            LocalDateTime effectiveAt){
        ExchangeRate exchangeRate =  new ExchangeRate();
        exchangeRate.setCurrency(currency);
        exchangeRate.setRate(rate);
        exchangeRate.setEffectiveAt(effectiveAt);

        currencyEngine.addRate(exchangeRate);
    }


}
