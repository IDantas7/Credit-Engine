package br.com.srm.creditengine.application.service;

import br.com.srm.creditengine.domain.exception.ExchangeRateNotFoundException;
import br.com.srm.creditengine.domain.model.*;
import br.com.srm.creditengine.domain.service.PricingCalculator;
import br.com.srm.creditengine.domain.strategy.ChequePricingStrategy;
import br.com.srm.creditengine.domain.strategy.DuplicataPricingStrategy;
import br.com.srm.creditengine.infrastructure.fx.CurrencyConverter;
import br.com.srm.creditengine.infrastructure.fx.CurrencyEngine;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PricingApplicationServiceTest {

    //Golden Case ##C1
    @Test
    void shouldSimulateDuplicataInBrl() {

        DuplicataPricingStrategy strategy = new DuplicataPricingStrategy();
        BigDecimal baseRate = new BigDecimal("0.01");
        PricingCalculator pricingCalculator = new PricingCalculator(List.of(strategy), baseRate);

        CurrencyEngine currencyEngine = new CurrencyEngine();
        CurrencyConverter currencyConverter = new CurrencyConverter();

        PricingApplicationService service =
                new PricingApplicationService(
                        pricingCalculator,
                        currencyEngine,
                        currencyConverter
                );

        BigDecimal expectedPresentValue = new BigDecimal("92859.94");
        BigDecimal expectedDiscount = new BigDecimal("7140.06");

        Receivable receivable = new Receivable();
        receivable.setReceivableType(ReceivableType.DUPLICATA_MERCANTIL);
        receivable.setFaceValue(new BigDecimal("100000"));
        receivable.setTermMonths(3);
        receivable.setCurrency(Currency.BRL);

        LocalDateTime referenceTime =
                LocalDateTime.of(2026, 9, 15, 14, 30);


        PricingResult result = service.simulate(receivable, referenceTime);


        assertEquals(expectedPresentValue, result.getPresentValueBrl());
        assertEquals(expectedDiscount, result.getDiscount());
        assertEquals(Currency.BRL, result.getPaymentCurrency());
        assertNull(result.getConvertedValue());
        assertNull(result.getExchangeRate());
    }

    //Golden Case ##C2
    @Test
    void shouldSimulateChequeInBrl() {

        ChequePricingStrategy strategy = new ChequePricingStrategy();
        BigDecimal baseRate = new BigDecimal("0.01");
        PricingCalculator pricingCalculator = new PricingCalculator(List.of(strategy), baseRate);

        CurrencyEngine currencyEngine = new CurrencyEngine();
        CurrencyConverter currencyConverter = new CurrencyConverter();

        PricingApplicationService service =
                new PricingApplicationService(
                        pricingCalculator,
                        currencyEngine,
                        currencyConverter
                );

        BigDecimal expectedPresentValue = new BigDecimal("23337.77");
        BigDecimal expectedDiscount = new BigDecimal("1662.23");

        Receivable receivable = new Receivable();
        receivable.setReceivableType(ReceivableType.CHEQUE_PRE_DATADO);
        receivable.setFaceValue(new BigDecimal("25000.00"));
        receivable.setTermMonths(2);
        receivable.setCurrency(Currency.BRL);

        LocalDateTime referenceTime =
                LocalDateTime.of(2026, 9, 15, 14, 30);


        PricingResult result = service.simulate(receivable, referenceTime);


        assertEquals(expectedPresentValue, result.getPresentValueBrl());
        assertEquals(expectedDiscount, result.getDiscount());
        assertEquals(Currency.BRL, result.getPaymentCurrency());
        assertNull(result.getConvertedValue());
        assertNull(result.getExchangeRate());
    }

    //Golden Case ##C3
    @Test
    void shouldSimulateDuplicataInUsd() {

        DuplicataPricingStrategy strategy = new DuplicataPricingStrategy();
        BigDecimal baseRate = new BigDecimal("0.01");
        PricingCalculator pricingCalculator = new PricingCalculator(List.of(strategy), baseRate);

        CurrencyEngine currencyEngine = new CurrencyEngine();
        CurrencyConverter currencyConverter = new CurrencyConverter();

        PricingApplicationService service =
                new PricingApplicationService(
                        pricingCalculator,
                        currencyEngine,
                        currencyConverter
                );

        ExchangeRate rate = new ExchangeRate();

        rate.setCurrency(Currency.USD);
        rate.setRate(new BigDecimal("5.4321"));
        rate.setEffectiveAt(
                LocalDateTime.of(2026, 9, 15, 14, 0)
        );

        currencyEngine.addRate(rate);

        BigDecimal expectedPresentValue = new BigDecimal("92859.94");
        BigDecimal expectedDiscount = new BigDecimal("7140.06");
        BigDecimal expectedConvertedValue = new BigDecimal("17094.67");
        BigDecimal expectedExchangeRate = new BigDecimal("5.4321");

        Receivable receivable = new Receivable();
        receivable.setReceivableType(ReceivableType.DUPLICATA_MERCANTIL);
        receivable.setFaceValue(new BigDecimal("100000"));
        receivable.setTermMonths(3);
        receivable.setCurrency(Currency.USD);

        LocalDateTime referenceTime =
                LocalDateTime.of(2026, 9, 15, 14, 30);


        PricingResult result = service.simulate(receivable, referenceTime);


        assertEquals(expectedPresentValue, result.getPresentValueBrl());
        assertEquals(expectedDiscount, result.getDiscount());
        assertEquals(Currency.USD, result.getPaymentCurrency());
        assertEquals(expectedConvertedValue, result.getConvertedValue());
        assertEquals(expectedExchangeRate, result.getExchangeRate());
    }

    //Test RunTimeException
    @Test
    void shouldThrowExceptionWhenSimulatingUsdWithoutExchangeRate() {

        DuplicataPricingStrategy strategy = new DuplicataPricingStrategy();

        BigDecimal baseRate = new BigDecimal("0.01");

        PricingCalculator pricingCalculator =
                new PricingCalculator(List.of(strategy), baseRate);

        CurrencyEngine currencyEngine = new CurrencyEngine();
        CurrencyConverter currencyConverter = new CurrencyConverter();

        PricingApplicationService service =
                new PricingApplicationService(
                        pricingCalculator,
                        currencyEngine,
                        currencyConverter
                );
        Receivable receivable = new Receivable();
        receivable.setReceivableType(ReceivableType.DUPLICATA_MERCANTIL);
        receivable.setFaceValue(new BigDecimal("100000"));
        receivable.setTermMonths(3);
        receivable.setCurrency(Currency.USD);

        LocalDateTime referenceTime =
                LocalDateTime.of(2026, 9, 15, 14, 30);

        assertThrows(ExchangeRateNotFoundException.class, () -> service.simulate(receivable, referenceTime));
    }

}
