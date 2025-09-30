package it.univaq.progettotesi.dto;

import it.univaq.progettotesi.entity.AssetType;
import it.univaq.progettotesi.entity.CommProtocol;

public record AssetDTO(
        Long id,
        String name,
        String brand,
        AssetType type,
        String model,
        CommProtocol commProtocol,
        String endPoint
) {
}
