package it.univaq.progettotesi.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.univaq.progettotesi.entity.Building;
import it.univaq.progettotesi.entity.BuildingConfig;
import it.univaq.progettotesi.repository.BuildingConfigRepository;
import it.univaq.progettotesi.repository.BuildingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuildingConfigService {


    private final BuildingConfigRepository buildingConfigRepository;
    private final BuildingRepository buildingRepository;
    private final BuildingConfigBuilder builder;
    private final ObjectMapper mapper;

    public BuildingConfigService(BuildingConfigBuilder builder, BuildingConfigRepository buildingConfigRepository, BuildingRepository buildingRepository, ObjectMapper mapper) {
        this.buildingConfigRepository = buildingConfigRepository;
        this.buildingRepository = buildingRepository;
        this.builder = builder;
        this.mapper = mapper;
    }

    @Transactional
    public BuildingConfig saveBuildingConfig(long buildingId) {

        var buildingRef = buildingRepository.getReferenceById(buildingId);
        // prendi la config se esiste, altrimenti creala
        var buildingConfig = buildingConfigRepository.findById(buildingId)
                .orElseGet(() -> new BuildingConfig(buildingId, buildingRef, 0));

        JsonNode payload = builder.buildPayload(buildingId);
        buildingConfig.setJson(payload.toString());
        buildingConfig.setVersion(buildingConfig.getVersion() + 1);

        return buildingConfigRepository.saveAndFlush(buildingConfig);
    }

    @Transactional
    public BuildingConfig getBuildingConfig(long buildingId) {
        return buildingConfigRepository.findById(buildingId)
                .orElseGet(()->this.saveBuildingConfig(buildingId));
    }

    @Transactional(readOnly = true)
    public JsonNode readJson(long buildingId) {
        var config =  buildingConfigRepository.findById(buildingId).orElseThrow(() -> new EntityNotFoundException("Building with id " + buildingId + " not found"));
        try {
            return mapper.readTree(config.getJson());
        }catch (Exception e){
            return null;
        }
    }

    @Transactional(readOnly = true)
    public String readJsonString(long buildingId) {
        return buildingConfigRepository.findById(buildingId)
                .map(BuildingConfig::getJson)
                .orElseThrow(() -> new EntityNotFoundException(
                        "BuildingConfig non trovata per buildingId=" + buildingId));
    }



}
