package it.univaq.progettotesi.api;

import it.univaq.progettotesi.service.BuildingConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/api/buildings")
public class BuildingConfigRestController {

    private final BuildingConfigService buildingConfigService;

    public BuildingConfigRestController(BuildingConfigService buildingConfigService) {
        this.buildingConfigService = buildingConfigService;
    }

    @GetMapping("/{buildingId}/config")
    public ResponseEntity<String> getBuildingConfig(@PathVariable long buildingId) {
        var json = buildingConfigService.getJson(buildingId);

        if (json == null) {
            return ResponseEntity.notFound().build(); // 404 se non trovato
        }

        return ResponseEntity.ok(json.toPrettyString());
    }
}

