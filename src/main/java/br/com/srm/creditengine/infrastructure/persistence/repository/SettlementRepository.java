package br.com.srm.creditengine.infrastructure.persistence.repository;

import br.com.srm.creditengine.infrastructure.persistence.entity.SettlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettlementRepository extends JpaRepository<SettlementEntity,Long> {
}
