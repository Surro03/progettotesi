package it.univaq.progettotesi.service;

import it.univaq.progettotesi.entity.*;
import it.univaq.progettotesi.repository.AssetRepository;
import it.univaq.progettotesi.repository.ClientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssetService {

    private final AssetRepository AssetRepository;
    private final BuildingConfigService buildingConfigService;
    private final ClientRepository clientRepository;

    public AssetService(AssetRepository AssetRepository,  BuildingConfigService configService,  ClientRepository clientRepository) {
        this.AssetRepository = AssetRepository;
        this.buildingConfigService = configService;
        this.clientRepository = clientRepository;
    }

    public List<Asset> findAll() {
        return AssetRepository.findAll();
    }

    public Page<Asset> findByBuildingId(Long buildingId,  Pageable pageable) {
        return AssetRepository.findByBuilding_Id(buildingId,  pageable);
    }
    public Optional<Asset> findById(Long id) {
        return AssetRepository.findById(id);
    }

    public Optional<Asset> findByBuildingIdAndAssetId(Long buildingId, Long assetId) {
        return AssetRepository.findByBuilding_IdAndId(buildingId, assetId);
    }

    @Transactional
    public Asset create(Admin user, Building building, String name, String brand,
                        AssetType type, String model, CommProtocol commProtocol, Client client) {

        Asset a = new Asset(user, building, name, brand, type, model, commProtocol, client);
        Asset saved = AssetRepository.saveAndFlush(a);
        buildingConfigService.saveBuildingConfig(building.getId());
        return saved;
    }

    @Transactional
    public Asset create(Admin user, Building building, String name, String brand,
                        AssetType type, String model, Client client) {

        Asset a = new Asset(user, building, name, brand, type, model, client);
        Asset saved = AssetRepository.saveAndFlush(a);
        buildingConfigService.saveBuildingConfig(building.getId());
        return saved;
    }

    public Asset save(Asset asset) {
        buildingConfigService.saveBuildingConfig(asset.getBuilding().getId());
        return AssetRepository.save(asset);
    }

    public Asset update(Long id,  Building building, String name, String brand, AssetType type, String model, CommProtocol commProtocol, Client c) {
        Asset a = AssetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asset non trovato: " + id));
        a.setBuilding(building);
        a.setName(name);
        a.setBrand(brand);
        a.setType(type);
        a.setModel(model);
        a.setCommProtocol(commProtocol);
        a.setClient(c);
        Asset asset = AssetRepository.save(a);
        buildingConfigService.saveBuildingConfig(building.getId());
        return asset;
    }

    public void delete(Long id) {
        Long buildingId = AssetRepository.findById(id).get().getBuilding().getId();
        AssetRepository.deleteById(id);
        buildingConfigService.saveBuildingConfig(buildingId);
    }
}
