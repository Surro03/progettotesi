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

}
