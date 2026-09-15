package br.com.srm.creditengine.domain.model;

import java.math.BigDecimal;

public class Receivable {
    private ReceivableType receivableType;
    private BigDecimal faceValue;
    private int termMonths;
    private Currency currency;
}
