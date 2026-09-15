package br.com.srm.creditengine.domain.strategy;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DuplicataPricingStrategyTest {

    @Test
    void shouldCalculatePriceWithDuplicata(){
        //arrange
        DuplicataPricingStrategy strategy = new DuplicataPricingStrategy();

        BigDecimal faceValue = new BigDecimal("100000");
        BigDecimal baseRate = new BigDecimal("0.01");
        int termMonths = 3;
        BigDecimal expectedPrice = new BigDecimal("92859.94");

        //act
        BigDecimal presentValue = strategy.calculatePresentValue(faceValue, termMonths, baseRate);

        System.out.println("Expected: " + expectedPrice);
        System.out.println("Actual: " + presentValue);

        //Aassert
        assertEquals(expectedPrice, presentValue);
    }
}
