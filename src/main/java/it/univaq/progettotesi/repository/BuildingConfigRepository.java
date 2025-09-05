package it.univaq.progettotesi.repository;

import it.univaq.progettotesi.entity.BuildingConfig;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildingConfigRepository extends JpaRepository<BuildingConfig, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from BuildingConfig c where c.buildingId = :id")
    Optional<BuildingConfig> findForUpdate(@Param("id") Long buildingId);
    // Con l'entity graph è possibile caricare anche l'entità padre
    @EntityGraph(attributePaths = "building")
    Optional<BuildingConfig> findByBuildingId(Long buildingId);
}
