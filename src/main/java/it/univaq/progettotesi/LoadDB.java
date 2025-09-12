package it.univaq.progettotesi;

import it.univaq.progettotesi.entity.*;
import it.univaq.progettotesi.repository.AssetRepository;
import it.univaq.progettotesi.repository.AdminRepository;
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
            AdminRepository userRepo,
            BuildingRepository buildingRepo,
            AssetRepository assetRepo,
            BuildingConfigService buildingConfigService
    ) {
        return args -> {
            // 1) User + Building
            Admin u = new Admin("Samuele", "Surricchio", "samuele2003@icloud.com", "{noop}ciao");
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

            // 3) Clienti fittizi collegati al building "Cinque"
            Client c1 = new Client("Mario", "Rossi", "mario.rossi@example.com", "{noop}password", b);
            Client c2 = new Client("Giulia", "Bianchi", "giulia.bianchi@example.com", "{noop}password", b);
            Client c3 = new Client("Luca", "Verdi", "luca.verdi@example.com", "{noop}password", b);
            Client c4 = new Client("Sara", "Neri", "sara.neri@example.com", "{noop}password", b);
            Client c5 = new Client("Paolo", "Gialli", "paolo.gialli@example.com", "{noop}password", b);

            // Aggiungo alla lista (cascade ALL da Building → Client)
            b.getClients().addAll(java.util.List.of(c1, c2, c3, c4, c5));
            // Re-salvo il building per propagare il persist dei client
            b = buildingRepo.saveAndFlush(b);
            log.info("Preloaded {} clients for building {}", b.getClients().size(), b.getName());

            // 4) Rigenera e salva la BuildingConfig
            var cfg = buildingConfigService.saveBuildingConfig(b.getId());

            // 5) Log di controllo
            log.info("Generated BuildingConfig v{} for building {} at {}",
                    cfg.getVersion(), b.getId(), cfg.getUpdatedAt());
            log.info("Config JSON: {}", cfg.getJson());
        };
    }
}
