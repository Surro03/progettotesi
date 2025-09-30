package it.univaq.progettotesi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
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
    @JoinColumn(name = "admin_id")
    @Getter
    @JsonIgnore
    private Admin admin;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @Getter
    @Setter
    private Client client;

    @ManyToOne(optional=false)
    @JoinColumn(name = "building_id")
    @Getter
    @JsonIgnore
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

    @Column()
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    private CommProtocol commProtocol; //e.g. MODBUS...

    public Asset() {
        //vuoto per JPA
    }

    public Asset(Admin admin, Building building, String name, String brand, AssetType type, String model, CommProtocol commProtocol, Client client) {
        this.admin = admin;
        this.building = building;
        this.name = name;
        this.brand = brand;
        this.type = type;
        this.model = model;
        this.commProtocol = commProtocol;
        this.client = client;
    }

    @JsonProperty("buildingId") // lo vedrai nel JSON come "buildingId"
    public Long getBuildingId() {
        return (building != null) ? building.getId() : null;
    }








}
