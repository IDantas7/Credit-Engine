package br.com.srm.creditengine.infrastructure.persistence.repository;

import br.com.srm.creditengine.infrastructure.persistence.entity.SimulationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationRepository extends JpaRepository<SimulationEntity, Long> {
}
