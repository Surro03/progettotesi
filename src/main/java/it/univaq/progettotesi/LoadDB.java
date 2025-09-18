package it.univaq.progettotesi;

import it.univaq.progettotesi.entity.Admin;
import it.univaq.progettotesi.entity.Asset;
import it.univaq.progettotesi.entity.AssetType;
import it.univaq.progettotesi.entity.Building;
import it.univaq.progettotesi.entity.Client;
import it.univaq.progettotesi.entity.CommProtocol;
import it.univaq.progettotesi.repository.AdminRepository;
import it.univaq.progettotesi.repository.AssetRepository;
import it.univaq.progettotesi.repository.BuildingRepository;
import it.univaq.progettotesi.service.BuildingConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    private static LocalDate d(int year, int month1to12, int day) {
        return LocalDate.of(year, month1to12, day) ;
    }

    @Bean
    CommandLineRunner initDatabase(
            AdminRepository adminRepo,
            BuildingRepository buildingRepo,
            AssetRepository assetRepo,
            BuildingConfigService buildingConfigService
    ) {
        return args -> {
            // 1) Admin
            Admin admin = new Admin(
                    "Samuele", "Surricchio", "samuele2003@icloud.com",
                    "{noop}ciao",
                    d(2003, 5, 12),
                    "+393331234567"
            );
            admin = adminRepo.save(admin);
            log.info("Preloaded admin: {}", admin.getEmail());

            // 2) Building
            Building building = new Building(admin, "Cinque", "Via Cervaro 15");
            building = buildingRepo.save(building);
            log.info("Preloaded building: {} (owner: {})", building.getName(), admin.getEmail());

            // 3) Assets di esempio
            Asset a1 = new Asset(
                    admin, building,
                    "Wallbox 7kW", "ABB",
                    AssetType.EVSE, "WB-7",
                    CommProtocol.OCPP,
                    "wss://ocpp.example/ws"
            );
            Asset a2 = new Asset(
                    admin, building,
                    "Inverter 10kW", "Fronius",
                    AssetType.INVERTER, "Primo-10",
                    CommProtocol.MODBUS,
                    "tcp://192.168.1.50:502"
            );
            Asset a3 = new Asset(
                    admin, building,
                    "Access Point", "Ubiquiti",
                    AssetType.WIFI, "UAP-AC-Lite",
                    CommProtocol.HTTP,
                    "http://192.168.1.10"
            );

            assetRepo.saveAll(List.of(a1, a2, a3));
            assetRepo.flush();
            log.info("Preloaded {} assets for building {}", 3, building.getName());

            // 4) Clients collegati al building (usano il costruttore nuovo)
            Client c1 = new Client("Mario",  "Rossi",  "mario.rossi@example.com",  "{noop}password",
                    building, d(1990, 1, 15), "+393400000001");
            Client c2 = new Client("Giulia", "Bianchi","giulia.bianchi@example.com","{noop}password",
                    building, d(1992, 7, 3),  "+393400000002");
            Client c3 = new Client("Luca",   "Verdi",  "luca.verdi@example.com",   "{noop}password",
                    building, d(1988, 11, 20), "+393400000003");
            Client c4 = new Client("Sara",   "Neri",   "sara.neri@example.com",    "{noop}password",
                    building, d(1995, 4, 9),   "+393400000004");
            Client c5 = new Client("Paolo",  "Gialli", "paolo.gialli@example.com", "{noop}password",
                    building, d(1993, 9, 27),  "+393400000005");

            // Aggiungo i client alla collection del building e persisto via cascade
            building.getClients().addAll(List.of(c1, c2, c3, c4, c5));
            building = buildingRepo.saveAndFlush(building);
            log.info("Preloaded {} clients for building {}", building.getClients().size(), building.getName());

            // 5) Rigenera e salva la BuildingConfig
            var cfg = buildingConfigService.saveBuildingConfig(building.getId());

            // 6) Log di controllo
            log.info("Generated BuildingConfig v{} for building {} at {}",
                    cfg.getVersion(), building.getId(), cfg.getUpdatedAt());
            log.info("Config JSON: {}", cfg.getJson());
        };
    }
}
