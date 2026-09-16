package br.com.srm.creditengine.application.service;

import br.com.srm.creditengine.application.result.SettlementResult;
import br.com.srm.creditengine.domain.exception.SimulationNotFoundException;
import br.com.srm.creditengine.infrastructure.persistence.entity.SettlementEntity;
import br.com.srm.creditengine.infrastructure.persistence.entity.SimulationEntity;
import br.com.srm.creditengine.infrastructure.persistence.repository.SettlementRepository;
import br.com.srm.creditengine.infrastructure.persistence.repository.SimulationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

        SettlementEntity settlement = SettlementEntity.builder()
                .idempotencyKey(idempotencyKey)
                .simulation(simulationEntity)
                .build();

        SettlementEntity savedSettlement =
                settlementRepository.save(settlement);
        return new SettlementResult(
                savedSettlement.getId(),
                savedSettlement.getSimulation().getId(),
                savedSettlement.getIdempotencyKey(),
                savedSettlement.getSettledAt()
        );

    }
}
