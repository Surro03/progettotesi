package it.univaq.progettotesi.repository;

import it.univaq.progettotesi.entity.BuildingConfig;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildingConfigRepository extends JpaRepository<BuildingConfig, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE) //Questo blocca la riga selezionata, impedendo ad altre transazioni di leggerla o aggiornarla finché la transazione corrente non viene completata
    @Query("select c from BuildingConfig c where c.buildingId = :id")
    Optional<BuildingConfig> findForUpdate(@Param("id") Long buildingId);
    // Con l'entity graph è possibile caricare anche l'entità padre
    @EntityGraph(attributePaths = "building")
    @Lock(LockModeType.PESSIMISTIC_WRITE) //Questo blocca la riga selezionata, impedendo ad altre transazioni di leggerla o aggiornarla finché la transazione corrente non viene completata
    Optional<BuildingConfig> findByBuildingId(Long buildingId);

    void deleteByBuilding_Id(Long buildingId);

}
