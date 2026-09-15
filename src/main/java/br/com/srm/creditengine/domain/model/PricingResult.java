package br.com.srm.creditengine.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PricingResult {
    private BigDecimal presentValueBrl;
    private BigDecimal discount;
    private Currency paymentCurrency;
    private BigDecimal convertedValue;
    private BigDecimal exchangeRate;

}
