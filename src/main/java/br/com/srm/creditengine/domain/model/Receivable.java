package br.com.srm.creditengine.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Receivable {
    private ReceivableType receivableType;
    private BigDecimal faceValue;
    private int termMonths;
    private Currency currency;
}
