package br.com.srm.creditengine.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ExchangeRate {
    private Currency currency;
    private BigDecimal rate;
    private LocalDateTime effectiveAt;
}
