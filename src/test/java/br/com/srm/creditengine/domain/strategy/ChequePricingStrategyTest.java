package br.com.srm.creditengine.domain.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChequePricingStrategyTest {

    @Test
    void shouldCalculatePriceWithCheque(){
        //arrange
        ChequePricingStrategy strategy = new ChequePricingStrategy();

        BigDecimal faceValue = new BigDecimal("25000");
        BigDecimal baseRate = new BigDecimal("0.01");
        int termMonths = 2;
        BigDecimal expectedPrice = new BigDecimal("23337.77");

        //act
        BigDecimal presentValue = strategy.calculatePresentValue(faceValue, termMonths, baseRate);

        System.out.println("Expected: " + expectedPrice);
        System.out.println("Actual: " + presentValue);

        //Aassert
        assertEquals(expectedPrice, presentValue);
    }
}
