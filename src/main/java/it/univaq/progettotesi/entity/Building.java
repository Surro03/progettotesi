package it.univaq.progettotesi.entity;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Column(nullable=false, name = "building_id")
    private Long id;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private List<Client> clients = new ArrayList<>();

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private List<Asset> assets = new ArrayList<>();

    // molti building per un admin
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    @Getter @Setter
    private Admin admin;


    @Column(nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(nullable=false)
    @Getter
    @Setter
    private String address;

    @Column(nullable=false)
    @Getter
    @Setter
    private String energeticClass;

    @Column(nullable=false)
    @Getter
    @Setter
    private Integer apartments;     // Numero di unità abitative

    @Column(nullable=false)
    @Getter
    @Setter
    private Integer yearOfConstruction;

    @Column(nullable=false)
    @Getter
    @Setter
    private Integer numbersOfFloors;

    @Column(nullable=false)
    @Getter
    @Setter
    private Double surface;           // Superficie in metri quadrati

    // Coordinate per la mappa
    @Column(nullable=false)
    @Getter
    @Setter
    private Double latitude;

    @Column(nullable=false)
    @Getter
    @Setter
    private Double longitude;

    //@Column(nullable=false)
    //private String ifcIdentifier;

    public Building() {
        // no-args constructor richiesto da JPA
    }

    public Building(Admin user, String name, String address, String energeticClass, Integer apartments, Integer yearOfConstruction, Integer numbersOfFloors, Double surface, Double latitude, Double longitude) {
        this.admin = user;
        this.name = name;
        this.address = address;
        this.energeticClass = energeticClass;
        this.apartments = apartments;
        this.yearOfConstruction = yearOfConstruction;
        this.numbersOfFloors = numbersOfFloors;
        this.surface = surface;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void addClient(Client client) {
        this.clients.add(client);
    }

    public void addAsset(Asset asset) {
        this.assets.add(asset);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Building)) return false;

        return this.id != null && this.id.equals(((Building) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.admin.getId());
    }

    @Override
    public String toString() {
        return "Building{" +
                "id=" + this.id +
                ", admin= " + this.admin +
                ", name= " + this.name +
                ", address= "+ this.address +
                ", energeticClass= " + this.energeticClass +
                ", apartments= " + this.apartments +
                ", yearOfConstruction= " + this.yearOfConstruction +
                ", numbersOfFloors= " + this.numbersOfFloors +
                ", surface= " + this.surface +
                ", latitude= " + this.latitude +
                ", longitude= " + this.longitude +'}';
    }


}
