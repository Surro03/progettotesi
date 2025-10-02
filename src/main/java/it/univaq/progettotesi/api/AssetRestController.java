package it.univaq.progettotesi.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.univaq.progettotesi.dto.AssetDTO;
import it.univaq.progettotesi.entity.Asset;
import it.univaq.progettotesi.entity.AssetType;
import it.univaq.progettotesi.entity.CommProtocol;
import it.univaq.progettotesi.mapper.AssetMapper;
import it.univaq.progettotesi.service.AssetService;
import it.univaq.progettotesi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/api/assets") // base path comune a tutti gli endpoint di questo controller
public class AssetRestController {

    private final AssetService assetService;
    private final AssetMapper assetMapper;
    private final UserService userService;

    public AssetRestController(AssetService assetService, AssetMapper assetMapper, UserService userService, ObjectMapper objectMapper) {
        this.assetService = assetService;
        this.assetMapper = assetMapper;
        this.userService = userService;
    }

    // GET /api/assets/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AssetDTO> getAsset(@PathVariable Long id) {
        return assetService.findById(id).map(entity -> ResponseEntity.ok(assetMapper.toDto(entity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AssetDTO> createAsset(@RequestBody AssetDTO dto) {

        var admin = userService.findAdminByEmail(dto.getAdminEmail());
        if (admin.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        var client = userService.findByClientEmail(dto.getClientEmail());
        if (client.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        Asset asset = assetService.create(
                admin.get(),
                client.get().getBuilding(),
                dto.getAssetName(),
                dto.getBrand(),
                AssetType.valueOf(dto.getType().toUpperCase()),
                dto.getModel(),
                client.get()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 Created
                .body(assetMapper.toDto(asset));
    }



    // PUT /app/api/assets/{id}
    @PutMapping("/{id}")
    public ResponseEntity<AssetDTO> updateAsset(@PathVariable Long id, @RequestBody AssetDTO dto) {
        return assetService.findById(id)
                .map(existing -> {
                    existing.setName(dto.getAssetName());
                    existing.setBrand(dto.getBrand());
                    existing.setType(AssetType.valueOf(dto.getType().toUpperCase()));
                    existing.setModel(dto.getModel());
                    existing.setClient((userService.findClientByEmail(dto.getClientEmail()).get()));

                    Asset updated = assetService.save(existing);
                    return ResponseEntity.ok(assetMapper.toDto(updated));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    // DELETE /app/api/assets/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsset(@PathVariable Long id) {
        if (assetService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        assetService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
