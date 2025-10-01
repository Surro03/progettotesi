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
        if (admin.isEmpty()) throw new RuntimeException("Admin non trovato");

        var client = userService.findByClientEmail(dto.getClientEmail());
        if (client.isEmpty()) throw new RuntimeException("Client non trovato");

        Asset asset = assetService.create(
                admin.get(),
                client.get().getBuilding(),
                dto.getAssetName(),
                dto.getBrand(),
                AssetType.valueOf(dto.getType().toUpperCase()),
                dto.getModel(),
                CommProtocol.MODBUS,
                client.get()
        );

        AssetDTO responseDto = assetMapper.toDto(asset);

        // Restituisce 201 Created + il body con l’oggetto creato
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseDto);
    }


    // PUT /api/assets/{id}
    @PutMapping("/{id}")
    public Asset updateAsset(@PathVariable Long id, @RequestBody Asset asset) {
        Asset existing = assetService.findById(id)
                .orElseThrow(() -> new RuntimeException("Asset non trovato"));
        existing.setName(asset.getName());
        existing.setBrand(asset.getBrand());
        existing.setType(asset.getType());
        existing.setModel(asset.getModel());
        existing.setCommProtocol(asset.getCommProtocol());
        return assetService.save(existing);
    }

    // DELETE /api/assets/{id}
    @DeleteMapping("/{id}")
    public void deleteAsset(@PathVariable Long id) {
        assetService.delete(id);
    }
}
