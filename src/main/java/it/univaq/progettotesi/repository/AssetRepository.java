package it.univaq.progettotesi.repository;

import it.univaq.progettotesi.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>{

    List<Asset> findByBuildingId(Long buildingId);
    Boolean existsByNameAndBuildingId(String name, Long buildingId);

}
