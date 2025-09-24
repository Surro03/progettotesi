package it.univaq.progettotesi.service;

import it.univaq.progettotesi.entity.Admin;
import it.univaq.progettotesi.entity.Building;
import it.univaq.progettotesi.entity.Client;
import it.univaq.progettotesi.entity.User;
import it.univaq.progettotesi.repository.AssetRepository;
import it.univaq.progettotesi.repository.BuildingConfigRepository;
import it.univaq.progettotesi.repository.BuildingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BuildingService {
    private final BuildingRepository buildingRepository;
    private final BuildingConfigRepository buildingConfigRepository;
    private final AssetRepository assetRepository;

    public BuildingService(BuildingRepository buildingRepository, AssetRepository assetRepository,  BuildingConfigRepository buildingConfigRepository) {
        this.buildingRepository = buildingRepository;
        this.assetRepository = assetRepository;
        this.buildingConfigRepository = buildingConfigRepository;
    }

    public List<Building> findAll() {
        return buildingRepository.findAll();
    }

    public Optional<Building> findById(Long id) {
        return buildingRepository.findById(id);
    }

    public Page<Building> findByAdminId(Long id,  Pageable pageable) {
        return buildingRepository.findByAdmin_Id(id, pageable);
    }

    public Building create(Admin user, String name, String address) {
        Building b = new Building(user, name, address);
        return buildingRepository.save(b);
    }

    public Building save(Building building) {
        return buildingRepository.save(building);
    }

    public Building update(Long id, String name, String address) {
        Building b = buildingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Building non trovato: " + id));
        b.setName(name);
        b.setAddress(address);
        return buildingRepository.save(b);
    }

    @Transactional
    public void delete(Long id) {
        assetRepository.deleteByBuilding_Id(id);
        buildingConfigRepository.deleteByBuilding_Id(id);
        buildingRepository.deleteById(id);
    }
}
