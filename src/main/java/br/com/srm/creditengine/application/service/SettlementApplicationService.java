package br.com.srm.creditengine.application.service;

import br.com.srm.creditengine.application.result.SettlementResult;
import br.com.srm.creditengine.domain.exception.IdempotencyConflictException;
import br.com.srm.creditengine.domain.exception.SimulationAlreadySettledException;
import br.com.srm.creditengine.domain.exception.SimulationNotFoundException;
import br.com.srm.creditengine.infrastructure.persistence.entity.SettlementEntity;
import br.com.srm.creditengine.infrastructure.persistence.entity.SimulationEntity;
import br.com.srm.creditengine.infrastructure.persistence.repository.SettlementRepository;
import br.com.srm.creditengine.infrastructure.persistence.repository.SimulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SettlementApplicationService {

    private final SettlementRepository settlementRepository;
    private final SimulationRepository simulationRepository;


    @Transactional
    public SettlementResult settle(Long simulationId,String idempotencyKey){
        Optional<SettlementEntity> existingSettlement =
                settlementRepository.findByIdempotencyKey(idempotencyKey);

        if (existingSettlement.isPresent()) {
            SettlementEntity settlementEntity = existingSettlement.get();
            if (!Objects.equals(settlementEntity.getSimulation().getId(), simulationId)) {
                throw new IdempotencyConflictException(idempotencyKey);
            }
            return new SettlementResult(
                    settlementEntity.getId(),
                    settlementEntity.getSimulation().getId(),
                    settlementEntity.getIdempotencyKey(),
                    settlementEntity.getSettledAt()

            );
        }
        Optional<SimulationEntity>  existingSimulation =
                simulationRepository.findById(simulationId);
        if (existingSimulation.isEmpty()){
            throw new SimulationNotFoundException(simulationId);
        }
        SimulationEntity simulationEntity = existingSimulation.get();

        Optional<SettlementEntity> existingSettlementBySimulation  = settlementRepository.findBySimulationId(simulationEntity.getId());
        if (existingSettlementBySimulation.isPresent()){
            throw new SimulationAlreadySettledException(simulationId);
        }

        SettlementEntity settlement = SettlementEntity.builder()
                .idempotencyKey(idempotencyKey)
                .simulation(simulationEntity)
                .build();

        SettlementEntity savedSettlement =
                settlementRepository.saveAndFlush(settlement);
        return new SettlementResult(
                savedSettlement.getId(),
                savedSettlement.getSimulation().getId(),
                savedSettlement.getIdempotencyKey(),
                savedSettlement.getSettledAt()
        );

    }
    @Transactional
    public List<SettlementResult> findAll(){
        List<SettlementEntity> settlementEntities = settlementRepository.findAll();

        return settlementEntities.stream()
                .map(settlement -> new SettlementResult(
                        settlement.getId(),
                        settlement.getSimulation().getId(),
                        settlement.getIdempotencyKey(),
                        settlement.getSettledAt()
                ))
                .toList();
    }
}
