package br.com.srm.creditengine.domain.service;

import br.com.srm.creditengine.domain.exception.PricingStrategyNotFoundException;
import br.com.srm.creditengine.domain.model.Receivable;
import br.com.srm.creditengine.domain.model.ReceivableType;
import br.com.srm.creditengine.domain.strategy.ChequePricingStrategy;
import br.com.srm.creditengine.domain.strategy.DuplicataPricingStrategy;
import br.com.srm.creditengine.domain.strategy.PricingStrategy;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PricingCalculatorTest {

    @Test
    void shouldCalculatePresentValueUsingDuplicataStrategy() {
        DuplicataPricingStrategy duplicataStrategy = new DuplicataPricingStrategy();

        ChequePricingStrategy chequeStrategy = new ChequePricingStrategy();

        List<PricingStrategy> strategies = List.of(duplicataStrategy, chequeStrategy);

        BigDecimal baseRate = new BigDecimal("0.01");

        PricingCalculator calculator = new PricingCalculator(strategies, baseRate);

        Receivable receivable = new Receivable();
        receivable.setReceivableType(ReceivableType.DUPLICATA_MERCANTIL);
        receivable.setFaceValue(new BigDecimal("100000"));
        receivable.setTermMonths(3);

        BigDecimal expectedValue = new BigDecimal("92859.94");

        BigDecimal actualValue = calculator.calculatePresentValue(receivable);

        assertEquals(expectedValue, actualValue);

    }

    @Test
    void shouldCalculatePresentValueUsingChequeStrategy() {
        DuplicataPricingStrategy duplicataStrategy = new DuplicataPricingStrategy();

        ChequePricingStrategy chequeStrategy = new ChequePricingStrategy();

        List<PricingStrategy> strategies = List.of(duplicataStrategy, chequeStrategy);

        BigDecimal baseRate = new BigDecimal("0.01");

        PricingCalculator calculator = new PricingCalculator(strategies, baseRate);

        Receivable receivable = new Receivable();
        receivable.setReceivableType(ReceivableType.CHEQUE_PRE_DATADO);
        receivable.setFaceValue(new BigDecimal("25000"));
        receivable.setTermMonths(2);

        BigDecimal expectedValue = new BigDecimal("23337.77");

        BigDecimal actualValue = calculator.calculatePresentValue(receivable);

        assertEquals(expectedValue, actualValue);


    }

    @Test
    void shouldThrowExceptionWhenStrategyIsNotFound() {
        DuplicataPricingStrategy duplicataStrategy = new DuplicataPricingStrategy();

        List<PricingStrategy> strategies = List.of(duplicataStrategy);

        BigDecimal baseRate = new BigDecimal("0.01");

        PricingCalculator calculator = new PricingCalculator(strategies, baseRate);

        Receivable receivable = new Receivable();
        receivable.setReceivableType(ReceivableType.CHEQUE_PRE_DATADO);

        assertThrows(PricingStrategyNotFoundException.class, () -> calculator.calculatePresentValue(receivable));

    }
}
