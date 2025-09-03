package it.univaq.progettotesi.service;

import it.univaq.progettotesi.entity.Building;
import it.univaq.progettotesi.entity.User;
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

    public BuildingService(BuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    public List<Building> findAll() {
        return buildingRepository.findAll();
    }

    public Optional<Building> findById(Long id) {
        return buildingRepository.findById(id);
    }

    public Page<Building> findByUserId(Long id,  Pageable pageable) {
        return buildingRepository.findByUser_Id(id, pageable);
    }

    public Building create(User user, String name, String address) {
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

    public void delete(Long id) {
        buildingRepository.deleteById(id);
    }
}
