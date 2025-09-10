package it.univaq.progettotesi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clients")
public class Client extends User {

    // molti client per un building → owning side
    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    @Getter @Setter
    private Building building;

    public Client() {
        super();
    }

    public Client(String name, String surname, String email, String password, Building building) {
        super(name,surname,email,password, "CLIENT");
        this.building = building;
    }


}
