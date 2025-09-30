package it.univaq.progettotesi.dto;

import lombok.Data;

@Data
public class AssetDTO {

    private Long id;
    private String clientEmail;
    private String adminEmail;
    private String assetName;
    private String brand;
    private String type;
    private String model;

import it.univaq.progettotesi.entity.AssetType;
import it.univaq.progettotesi.entity.CommProtocol;

public record AssetRestDTO(
        Long id,
        String name,
        String brand,
        AssetType type,
        String model,
        CommProtocol commProtocol,
        String endPoint
) {
}
