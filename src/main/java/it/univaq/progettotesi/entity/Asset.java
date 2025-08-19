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
    private User user;

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
    @Getter
    @Setter
    private String type;  //e.g. INVERTER, EVSE...

    @Column(nullable=false)
    @Getter
    @Setter
    private String model;

    @Column(nullable=false)
    @Getter
    @Setter
    private String commProtocol; //e.g. MODBUS...

    @Column(nullable=false)
    @Getter
    @Setter
    private String endpoint;









}
