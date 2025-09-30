package it.univaq.progettotesi.mapper;

import it.univaq.progettotesi.dto.AssetDTO;
import it.univaq.progettotesi.entity.*;

import org.springframework.stereotype.Component;


@Component
public class AssetMapper {


    public AssetDTO toDto(Asset asset) {
        if (asset == null) {
            return null;
        }

        AssetDTO dto = new AssetDTO();

        // Mappa gli identificativi dalle entità correlate
        // Si presuppone che le entità non siano null, dato che sono `optional=false`
        dto.setClientEmail(asset.getClient().getEmail());
        dto.setAdminEmail(asset.getAdmin().getEmail());

        // Mappa i campi semplici
        dto.setAssetName(asset.getName());
        dto.setBrand(asset.getBrand());
        dto.setModel(asset.getModel());
        dto.setId(asset.getId());

        // Converte l'enum AssetType in String
        if (asset.getType() != null) {
            dto.setType(asset.getType().name());
        }

        return dto;
    }


    public Asset toEntity(AssetDTO dto, Admin admin, Client client, Building building) {
        if (dto == null) {
            return null;
        }

        Asset asset = new Asset();

        // Imposta le relazioni con le entità fornite
        asset.setAdmin(admin);
        asset.setClient(client);
        asset.setBuilding(building);

        // Mappa i campi semplici
        asset.setName(dto.getAssetName());
        asset.setBrand(dto.getBrand());
        asset.setModel(dto.getModel());
        asset.setId(null);

        // Converte la stringa in un enum AssetType, gestendo possibili errori
        if (dto.getType() != null && !dto.getType().trim().isEmpty()) {
            try {
                // Converte la stringa in maiuscolo per matchare le costanti dell'enum
                asset.setType(AssetType.valueOf(dto.getType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Gestisce il caso in cui la stringa non corrisponda a nessun valore dell'enum.
                // Qui si potrebbe lanciare un'eccezione personalizzata per indicare un dato non valido.
                throw new IllegalArgumentException("Tipo di asset non valido: " + dto.getType());
            }
        }

        return asset;
    }
}

