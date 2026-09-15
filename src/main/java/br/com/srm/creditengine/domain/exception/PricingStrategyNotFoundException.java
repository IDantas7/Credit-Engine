package br.com.srm.creditengine.domain.exception;

public class PricingStrategyNotFoundException extends RuntimeException {
    public PricingStrategyNotFoundException(String message) {
        super(message);
    }
}
