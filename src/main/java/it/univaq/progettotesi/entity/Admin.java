package it.univaq.progettotesi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "admins")
public class Admin extends User{

    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    private List<Building> buildings = new ArrayList<>();

    public Admin(String name, String surname, String email, String password) {
        super(name,surname,email,password, "ADMIN");
    }

    public Admin(){}

    public void addBuilding(Building building) {
        this.buildings.add(building);
    }
}
