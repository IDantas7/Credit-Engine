package br.com.srm.creditengine.application.service;

import br.com.srm.creditengine.application.result.SimulationResult;
import br.com.srm.creditengine.domain.model.Currency;
import br.com.srm.creditengine.domain.model.ExchangeRate;
import br.com.srm.creditengine.domain.model.Receivable;
import br.com.srm.creditengine.domain.model.ReceivableType;
import br.com.srm.creditengine.domain.service.PricingCalculator;
import br.com.srm.creditengine.infrastructure.fx.CurrencyConverter;
import br.com.srm.creditengine.infrastructure.fx.CurrencyEngine;
import br.com.srm.creditengine.infrastructure.persistence.entity.SimulationEntity;
import br.com.srm.creditengine.infrastructure.persistence.repository.SimulationRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class SimulationApplicationServiceTest {

    @Mock
    private PricingCalculator pricingCalculator;

    @Mock
    private SimulationRepository simulationRepository;

    @Mock
    private CurrencyEngine currencyEngine;

    @Mock
    private CurrencyConverter currencyConverter;

    @InjectMocks
    private SimulationApplicationService simulationApplicationService;

    @Test
    void shouldCreateSimulationInBrl(){
        Receivable receivable = new Receivable();
        receivable.setReceivableType(ReceivableType.DUPLICATA_MERCANTIL);
        receivable.setFaceValue(new BigDecimal("100000"));
        receivable.setTermMonths(3);
        receivable.setCurrency(Currency.BRL);

        when(pricingCalculator.calculatePresentValue(receivable)).thenReturn(new BigDecimal("92859.94")) ;
        when(pricingCalculator.getBaseRate()).thenReturn(new BigDecimal("0.01"));
        when(pricingCalculator.getSpread(receivable.getReceivableType())).thenReturn(new BigDecimal("0.015"));

        SimulationEntity savedSimulation  = SimulationEntity.builder()
                .id(1L)
                .receivableType(ReceivableType.DUPLICATA_MERCANTIL)
                .faceValue(new BigDecimal("100000"))
                .termMonths(3)
                .paymentCurrency(Currency.BRL)
                .baseRate(new BigDecimal("0.01"))
                .spread(new BigDecimal("0.015"))
                .presentValue(new BigDecimal("92859.94"))
                .discount(new BigDecimal("7140.06"))
                .build();

        when(simulationRepository.saveAndFlush(any(SimulationEntity.class))).thenReturn(savedSimulation);

        SimulationResult result = simulationApplicationService.simulate(receivable);

        assertEquals(1L, result.simulationId());
        assertEquals(ReceivableType.DUPLICATA_MERCANTIL, result.receivableType());
        assertEquals(new BigDecimal("100000"), result.faceValue());
        assertEquals(3, result.termMonths());
        assertEquals(Currency.BRL, result.paymentCurrency());
        assertEquals(new BigDecimal("0.01"), result.baseRate());
        assertEquals(new BigDecimal("0.015"), result.spread());
        assertEquals(new BigDecimal("92859.94"), result.presentValue());
        assertEquals(new BigDecimal("7140.06"), result.discount());
        assertNull(result.fxRate());
        assertNull(result.convertedValue());

        verify(simulationRepository).saveAndFlush(any(SimulationEntity.class));
        verifyNoInteractions(currencyConverter, currencyEngine);

    }

    @Test
    void shouldCreateSimulationInUsd(){
        Receivable receivable = new Receivable();
        receivable.setReceivableType(ReceivableType.DUPLICATA_MERCANTIL);
        receivable.setFaceValue(new BigDecimal("100000"));
        receivable.setTermMonths(3);
        receivable.setCurrency(Currency.USD);

        when(pricingCalculator.calculatePresentValue(receivable))
                .thenReturn(new BigDecimal("92859.94"));

        when(pricingCalculator.getBaseRate())
                .thenReturn(new BigDecimal("0.01"));

        when(pricingCalculator.getSpread(receivable.getReceivableType()))
                .thenReturn(new BigDecimal("0.015"));

        ExchangeRate exchangeRate = mock(ExchangeRate.class);

        when(exchangeRate.getRate())
                .thenReturn(new BigDecimal("5.4321"));

        when(currencyEngine.getExchangeRate(
                eq(Currency.USD),
                any(LocalDateTime.class)
        )).thenReturn(exchangeRate);

        when(currencyConverter.convert(
                new BigDecimal("92859.94"),
                exchangeRate
        )).thenReturn(new BigDecimal("17094.67"));

        SimulationEntity savedSimulation = SimulationEntity.builder()
                .id(2L)
                .receivableType(ReceivableType.DUPLICATA_MERCANTIL)
                .faceValue(new BigDecimal("100000"))
                .termMonths(3)
                .paymentCurrency(Currency.USD)
                .baseRate(new BigDecimal("0.01"))
                .spread(new BigDecimal("0.015"))
                .presentValue(new BigDecimal("92859.94"))
                .discount(new BigDecimal("7140.06"))
                .fxRate(new BigDecimal("5.4321"))
                .convertedValue(new BigDecimal("17094.67"))
                .build();

        when(simulationRepository.saveAndFlush(any(SimulationEntity.class)))
                .thenReturn(savedSimulation);

        SimulationResult result =
                simulationApplicationService.simulate(receivable);

        assertEquals(2L, result.simulationId());
        assertEquals(
                ReceivableType.DUPLICATA_MERCANTIL,
                result.receivableType()
        );
        assertEquals(new BigDecimal("100000"), result.faceValue());
        assertEquals(3, result.termMonths());
        assertEquals(Currency.USD, result.paymentCurrency());
        assertEquals(new BigDecimal("0.01"), result.baseRate());
        assertEquals(new BigDecimal("0.015"), result.spread());
        assertEquals(new BigDecimal("92859.94"), result.presentValue());
        assertEquals(new BigDecimal("7140.06"), result.discount());
        assertEquals(new BigDecimal("5.4321"), result.fxRate());
        assertEquals(new BigDecimal("17094.67"), result.convertedValue());

        verify(currencyEngine).getExchangeRate(
                eq(Currency.USD),
                any(LocalDateTime.class)
        );

        verify(currencyConverter).convert(
                new BigDecimal("92859.94"),
                exchangeRate
        );

        verify(simulationRepository)
                .saveAndFlush(any(SimulationEntity.class));
    }

}

