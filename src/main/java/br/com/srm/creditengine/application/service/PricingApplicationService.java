package br.com.srm.creditengine.application.service;

import br.com.srm.creditengine.domain.model.Currency;
import br.com.srm.creditengine.domain.model.ExchangeRate;
import br.com.srm.creditengine.domain.model.PricingResult;
import br.com.srm.creditengine.domain.model.Receivable;
import br.com.srm.creditengine.domain.service.PricingCalculator;
import br.com.srm.creditengine.infrastructure.fx.CurrencyConverter;
import br.com.srm.creditengine.infrastructure.fx.CurrencyEngine;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PricingApplicationService {

    private final PricingCalculator pricingCalculator;
    private final CurrencyEngine currencyEngine;
    private final CurrencyConverter currencyConverter;

    public PricingApplicationService(
            PricingCalculator pricingCalculator,
            CurrencyEngine currencyEngine,
            CurrencyConverter currencyConverter) {

        this.pricingCalculator = pricingCalculator;
        this.currencyEngine = currencyEngine;
        this.currencyConverter = currencyConverter;
    }

    public PricingResult simulate(Receivable receivable, LocalDateTime referenceTime){
        BigDecimal presentValueBrl = pricingCalculator.calculatePresentValue(receivable);
        BigDecimal faceValue = receivable.getFaceValue();

        BigDecimal discount = faceValue.subtract(presentValueBrl);

        PricingResult result = new PricingResult();
        result.setPresentValueBrl(presentValueBrl);
        result.setDiscount(discount);
        result.setPaymentCurrency(receivable.getCurrency());

        if (receivable.getCurrency() == Currency.USD){
            ExchangeRate rate = currencyEngine.getExchangeRate(Currency.USD, referenceTime);

            BigDecimal convertedValue = currencyConverter.convert(presentValueBrl, rate);

            result.setConvertedValue(convertedValue);
            result.setExchangeRate(rate.getRate());
        }
        return  result;

    }
}
