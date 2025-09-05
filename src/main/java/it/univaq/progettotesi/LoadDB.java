package it.univaq.progettotesi;

import it.univaq.progettotesi.entity.*;
import it.univaq.progettotesi.repository.AssetRepository;
import it.univaq.progettotesi.repository.UserRepository;
import it.univaq.progettotesi.repository.BuildingRepository;
import it.univaq.progettotesi.service.BuildingConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepo,
            BuildingRepository buildingRepo,
            AssetRepository assetRepo,
            BuildingConfigService buildingConfigService
    ) {
        return args -> {
            // 1) User + Building
            User u = new User("Samuele", "Surricchio", "samuele2003@icloud.com", "{noop}ciao", "CLIENT");
            u = userRepo.save(u);
            log.info("Preloaded user: {}", u.getEmail());

            Building b = new Building(u, "Cinque", "Via Cervaro 15");
            b = buildingRepo.save(b);
            log.info("Preloaded building: {} (owner: {})", b.getName(), u.getEmail());

            // 2) Assets d'esempio
            Asset a1 = new Asset(u, b, "Wallbox 7kW", "ABB",
                    AssetType.EVSE, "WB-7", CommProtocol.OCPP, "wss://ocpp.example/ws");

            Asset a2 = new Asset(u, b, "Inverter 10kW", "Fronius",
                    AssetType.INVERTER, "Primo-10", CommProtocol.MODBUS, "tcp://192.168.1.50:502");

            Asset a3 = new Asset(u, b, "Access Point", "Ubiquiti",
                    AssetType.WIFI, "UAP-AC-Lite", CommProtocol.HTTP, "http://192.168.1.10");

            assetRepo.saveAll(java.util.List.of(a1, a2, a3));
            assetRepo.flush();

            // 3) Rigenera e salva la BuildingConfig
            var cfg = buildingConfigService.saveBuildingConfig(b.getId());

            // 4) Log di controllo
            log.info("Generated BuildingConfig v{} for building {} at {}",
                    cfg.getVersion(), b.getId(), cfg.getUpdatedAt());
            log.info("Config JSON: {}", cfg.getJson());
        };
    }
}

