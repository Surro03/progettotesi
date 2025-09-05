package it.univaq.progettotesi.service;

import it.univaq.progettotesi.entity.*;
import it.univaq.progettotesi.repository.AssetRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssetService {

    private final AssetRepository AssetRepository;
    private final BuildingConfigService buildingConfigService;

    public AssetService(AssetRepository AssetRepository,  BuildingConfigService configService) {
        this.AssetRepository = AssetRepository;
        this.buildingConfigService = configService;
    }

    public List<Asset> findAll() {
        return AssetRepository.findAll();
    }

    public List<Asset> findByBuildingId(Long buildingId) {
        return AssetRepository.findByBuilding_Id(buildingId);
    }

    public Optional<Asset> findById(Long id) {
        return AssetRepository.findById(id);
    }

    public Optional<Asset> findByBuildingIdAndAssetId(Long buildingId, Long assetId) {
        return AssetRepository.findByBuilding_IdAndId(buildingId, assetId);
    }

    @Transactional
    public Asset create(User user, Building building, String name, String brand,
                        AssetType type, String model, CommProtocol commProtocol, String endpoint) {
        Asset a = new Asset(user, building, name, brand, type, model, commProtocol, endpoint);
        Asset saved = AssetRepository.saveAndFlush(a);
        buildingConfigService.saveBuildingConfig(building.getId());
        return saved;
    }

    public Asset save(Asset asset) {
        buildingConfigService.saveBuildingConfig(asset.getBuilding().getId());
        return AssetRepository.save(asset);
    }

    public Asset update(Long id,  Building building, String name, String brand, AssetType type, String model, CommProtocol commProtocol, String endpoint) {
        Asset a = AssetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset non trovato: " + id));
        a.setBuilding(building);
        a.setName(name);
        a.setBrand(brand);
        a.setType(type);
        a.setModel(model);
        a.setCommProtocol(commProtocol);
        a.setEndpoint(endpoint);
        buildingConfigService.saveBuildingConfig(building.getId());
        return AssetRepository.save(a);
    }

    public void delete(Long id) {
        Long buildingId = AssetRepository.findById(id).get().getBuilding().getId();
        AssetRepository.deleteById(id);
        buildingConfigService.saveBuildingConfig(buildingId);
    }
}
