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

    public AssetService(AssetRepository AssetRepository) {
        this.AssetRepository = AssetRepository;
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
    public Asset create(User user, Building building, String name, String brand, AssetType type, String model, CommProtocol commProtocol, String endpoint) {
        Asset b = new Asset(user, building, name, brand, type, model, commProtocol, endpoint);
        return AssetRepository.save(b);
    }

    public Asset save(Asset asset) {
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
        return AssetRepository.save(a);
    }

    public void delete(Long id) {
        AssetRepository.deleteById(id);
    }
}
