package br.com.srm.creditengine.application.service;

import br.com.srm.creditengine.application.result.SettlementResult;
import br.com.srm.creditengine.domain.exception.SimulationNotFoundException;
import br.com.srm.creditengine.infrastructure.persistence.entity.SettlementEntity;
import br.com.srm.creditengine.infrastructure.persistence.entity.SimulationEntity;
import br.com.srm.creditengine.infrastructure.persistence.repository.SettlementRepository;
import br.com.srm.creditengine.infrastructure.persistence.repository.SimulationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class SettlementApplicationServiceTest {

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private SimulationRepository simulationRepository;

    @InjectMocks
    private SettlementApplicationService settlementApplicationService;



    @Test
    void shouldReturnExistingSettlementWhenIdempotencyKeyExists() {
        SimulationEntity simulation = SimulationEntity.builder()
                .id(10L)
                .build();

        SettlementEntity settle = SettlementEntity.builder()
                .simulation(simulation)
                .idempotencyKey("abc-123")
                .id(1L)
                .build();

        when(settlementRepository.findByIdempotencyKey("abc-123")).thenReturn(Optional.of(settle));

        SettlementResult result = settlementApplicationService.settle(10L, "abc-123");

        assertEquals(10L, result.simulationId());
        assertEquals("abc-123", result.idempotencyKey());
        assertEquals(1L, result.settlementId());

        verify(settlementRepository, never()).save(any(SettlementEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenSimulationNotFound() {
        when(settlementRepository.findByIdempotencyKey("abc-123")).thenReturn(Optional.empty());
        when(simulationRepository.findById(10L)).thenReturn(Optional.empty());



        assertThrows(SimulationNotFoundException.class, () -> settlementApplicationService.settle(10L, "abc-123"));
    }

    @Test
    void shouldCreateSettlementWhenSimulationExists(){
        SimulationEntity simulation = SimulationEntity.builder()
                .id(10L)
                .build();

        when(settlementRepository.findByIdempotencyKey("abc-123")).thenReturn(Optional.empty());
        when(simulationRepository.findById(10L)).thenReturn(Optional.of(simulation));

        SettlementEntity saveSettle = SettlementEntity.builder()
                .simulation(simulation)
                .idempotencyKey("abc-123")
                .id(1L)
                .build();

        when(settlementRepository.save(any(SettlementEntity.class))).thenReturn(saveSettle);

        SettlementResult result = settlementApplicationService.settle(10L, "abc-123");

        assertEquals(10L, result.simulationId());
        assertEquals("abc-123", result.idempotencyKey());
        assertEquals(1L, result.settlementId());

        verify(settlementRepository)
                .save(any(SettlementEntity.class));
    }

}
