package it.univaq.progettotesi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import it.univaq.progettotesi.repository.AssetRepository;
import it.univaq.progettotesi.repository.BuildingConfigRepository;
import org.springframework.stereotype.Component;

@Component
public class BuildingConfigBuilder {

    private final ObjectMapper om;
    private final AssetRepository assetRepo;
    private final BuildingConfigRepository buildingConfigRepository;

    public BuildingConfigBuilder(ObjectMapper om, AssetRepository assetRepo, BuildingConfigRepository buildingConfigRepository) {
        this.om = om;
        this.assetRepo = assetRepo;
        this.buildingConfigRepository = buildingConfigRepository;
    }

    public ObjectNode buildPayload(long buildingId) {
        var root = om.createObjectNode();
        if(buildingConfigRepository.findByBuildingId(buildingId).isEmpty()) {
              //è l'oggetto JSON radice, quello che poi verrà restituito
            root.put("configVersion", 1);
            root.put("buildingId", buildingId);
            root.put("generatedAt", java.time.Instant.now().toString());
        }
        else{//è l'oggetto JSON radice, quello che poi verrà restituito
            root.put("configVersion", buildingConfigRepository.findByBuildingId(buildingId).get().getVersion()+1);
            root.put("buildingId", buildingId);
            root.put("generatedAt", java.time.Instant.now().toString());
        }

        var caps = om.createObjectNode()   // sono le cose che l'edificio può fare, utile per capire quali parti di app attivare
                .put("evCharging", false)
                .put("solar", false)
                .put("wifi", false);

        var assetsArr = om.createArrayNode();  //aggiunge l'array con gli asset

        var assets = assetRepo.findByBuilding_Id(buildingId); // JOIN su asset_type
        for (var a : assets) {
            var code = a.getType().name();
            assetsArr.add(om.createObjectNode()
                    .put("name", a.getName())
                    .put("type", code)
                    .put("commProtocol",a.getCommProtocol().name()));
                    //.put("qty", a.getQty()));
            switch (a.getType()) {
                case INVERTER    -> caps.put("solar", true);
                case EVSE   -> caps.put("evCharging", true);
                case WIFI       -> caps.put("wifi", true);
            }
        }

        var hide = om.createArrayNode();  //tutte le cose non presenti verranno aggiunte al json hide per dire all'app mobile quali parti nascondere
        caps.properties().forEach(e -> { if (!e.getValue().asBoolean()) hide.add(e.getKey()); });

        var ui = om.createObjectNode();
        ui.set("hideSections", hide);
        ui.set("featureToggles", om.createObjectNode()  //features da attivare derivanti dalle flag precendenti. Ad esempio la possibilità di prenotare una colonnina e attivarla
                .put("startCharging", caps.get("evCharging").asBoolean()));

        root.set("capabilities", caps);
        root.set("assets", assetsArr);
        root.set("uiShow", ui);
        //root.put("i18nLocale", "it-IT");
        return root;
    }
}
