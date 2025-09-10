package it.univaq.progettotesi.entity;

import jakarta.persistence.*;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="building_assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name = "user_id")
    @Getter
    private Admin user;

    @ManyToOne(optional=false)
    @JoinColumn(name = "building_id")
    @Getter
    private Building building;

    @Column(nullable=false)
    @Getter
    @Setter
    private String name;

    @Column(nullable=false)
    @Getter
    @Setter
    private String brand;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private AssetType type;  //e.g. INVERTER, EVSE...

    @Column(nullable=false)
    @Getter
    @Setter
    private String model;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private CommProtocol commProtocol; //e.g. MODBUS...

    @Column
    @Getter
    @Setter
    private String endpoint;

    public Asset() {
        //vuoto per JPA
    }

    public Asset(Admin user, Building building, String name, String brand, AssetType type, String model, CommProtocol commProtocol, String endpoint) {
        this.user = user;
        this.building = building;
        this.name = name;
        this.brand = brand;
        this.type = type;
        this.model = model;
        this.commProtocol = commProtocol;
        this.endpoint = endpoint;
    }








}
