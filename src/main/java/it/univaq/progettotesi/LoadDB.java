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
import it.univaq.progettotesi.repository.ClientRepository;
import it.univaq.progettotesi.service.BuildingConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    private static LocalDate d(int year, int month1to12, int day) {
        return LocalDate.of(year, month1to12, day);
    }

    @Bean
    CommandLineRunner initDatabase(
            AdminRepository adminRepo,
            BuildingRepository buildingRepo,
            ClientRepository clientRepo,
            AssetRepository assetRepo,
            BuildingConfigService buildingConfigService,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // Evita doppio seed se ci sono già asset
            if (assetRepo.count() > 0) {
                log.info("Seed skipped: assets already present.");
                return;
            }

            // 1) Admin (se già esiste per email, riusa)
            Admin admin = adminRepo.findByEmail("samuele2003@icloud.com")
                    .orElseGet(() -> adminRepo.save(new Admin(
                            "Samuele", "Surricchio", "samuele2003@icloud.com",
                            passwordEncoder.encode("ciao"),
                            d(2003, 5, 12),
                            "+393331234567"
                    )));
            Admin admin1 = adminRepo.findByEmail("admin@admin.com")
                    .orElseGet(() -> adminRepo.save(new Admin(
                            "Admin", "Admin", "admin@admin.com",
                            passwordEncoder.encode("admin"),
                            d(2003, 5, 12),
                            "+393331234567"
                    )));
            admin1.setRole("ADMIN");
            adminRepo.save(admin1);
            log.info("Preloaded admin: {}", admin.getEmail());

            // 2) Building (se già esiste per nome, riusa)
            Building building = buildingRepo.findByName("Condominio Paradiso")
                    .orElseGet(() -> {
                        Building b = new Building();
                        b.setAdmin(admin);
                        b.setName("Condominio Paradiso");
                        b.setAddress("Via Garibaldi, 27, 20121 Milano, MI, Italia");
                        b.setEnergeticClass("A2");
                        b.setApartments(16);
                        b.setYearOfConstruction(1998);
                        b.setNumbersOfFloors(4);
                        b.setSurface(1850.5); // mq
                        b.setLatitude(45.4698);
                        b.setLongitude(9.1813);
                        return buildingRepo.save(b);
                    });
            log.info("Preloaded building: {} (owner: {})", building.getName(), admin.getEmail());

            // 3) Clients (persistiti esplicitamente)
            Client c1 = clientRepo.findByEmail("mario.rossi@example.com")
                    .orElseGet(() -> clientRepo.save(new Client(
                            "Mario", "Rossi", "mario.rossi@example.com", "{noop}password",
                            building, d(1990, 1, 15), "+393400000001"
                    )));
            Client c2 = clientRepo.findByEmail("giulia.bianchi@example.com")
                    .orElseGet(() -> clientRepo.save(new Client(
                            "Giulia", "Bianchi", "giulia.bianchi@example.com", "{noop}password",
                            building, d(1992, 7, 3), "+393400000002"
                    )));
            Client c3 = clientRepo.findByEmail("luca.verdi@example.com")
                    .orElseGet(() -> clientRepo.save(new Client(
                            "Luca", "Verdi", "luca.verdi@example.com", "{noop}password",
                            building, d(1988, 11, 20), "+393400000003"
                    )));
            Client c4 = clientRepo.findByEmail("sara.neri@example.com")
                    .orElseGet(() -> clientRepo.save(new Client(
                            "Sara", "Neri", "sara.neri@example.com", "{noop}password",
                            building, d(1995, 4, 9), "+393400000004"
                    )));
            Client c5 = clientRepo.findByEmail("paolo.gialli@example.com")
                    .orElseGet(() -> clientRepo.save(new Client(
                            "Paolo", "Gialli", "paolo.gialli@example.com", "{noop}password",
                            building, d(1993, 9, 27), "+393400000005"
                    )));

            log.info("Preloaded clients: {}", List.of(
                    c1.getEmail(), c2.getEmail(), c3.getEmail(), c4.getEmail(), c5.getEmail()
            ));

            // 4) Assets
            Asset a1 = new Asset(
                    admin, building,
                    "Wallbox 7kW", "ABB",
                    AssetType.EVSE, "WB-7",
                    CommProtocol.OCPP,
                    c1
            );
            Asset a2 = new Asset(
                    admin, building,
                    "Inverter 10kW", "Fronius",
                    AssetType.INVERTER, "Primo-10",
                    CommProtocol.MODBUS,
                    c2
            );
            Asset a3 = new Asset(
                    admin, building,
                    "Access Point", "Ubiquiti",
                    AssetType.WIFI, "Ubiquiti UAP-AC-Lite",
                    CommProtocol.HTTP,
                    c3
            );

            assetRepo.saveAll(List.of(a1, a2, a3));
            log.info("Preloaded {} assets for building {}", 3, building.getName());

            // 5) Rigenera e salva la BuildingConfig
            var cfg = buildingConfigService.saveBuildingConfig(building.getId());

            // 6) Log di controllo
            log.info("Generated BuildingConfig v{} for building {} at {}",
                    cfg.getVersion(), building.getId(), cfg.getUpdatedAt());
            log.info("Config JSON: {}", cfg.getJson());
        };
    }
}
