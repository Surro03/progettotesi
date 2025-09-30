package it.univaq.progettotesi.controller;

import it.univaq.progettotesi.dto.AssetDTO;
import it.univaq.progettotesi.entity.Asset;
import it.univaq.progettotesi.repository.AssetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/buildings")
public class AssetRestController {

    private final AssetRepository assetRepository;

    public AssetRestController(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @GetMapping("/{buildingId}/assets")
    public Page<AssetDTO> getAssets(
            @PathVariable Long buildingId,
            Pageable pageable
    ) {
        return assetRepository.findByBuilding_Id(buildingId, pageable)
                .map(this::toDTO);
    }

    private AssetDTO toDTO(Asset asset) {
        return new AssetDTO(
                asset.getId(),
                asset.getName(),
                asset.getBrand(),
                asset.getType(),
                asset.getModel(),
                asset.getCommProtocol(),
                asset.getEndpoint()
        );
    }
}
