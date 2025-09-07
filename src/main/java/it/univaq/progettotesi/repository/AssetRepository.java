package it.univaq.progettotesi.repository;

import it.univaq.progettotesi.entity.Asset;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>{

    Page<Asset> findByBuilding_Id(Long buildingId, Pageable pageable);
    Boolean existsByNameAndBuildingId(String name, Long buildingId);
    Optional<Asset> findByBuilding_IdAndId(Long buildingId, Long assetId);

}
