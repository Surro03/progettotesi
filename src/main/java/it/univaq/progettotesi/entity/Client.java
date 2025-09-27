package it.univaq.progettotesi.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "clients")
public class Client extends User {

    // molti client per un building → owning side
    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    @Getter @Setter
    @JsonIgnore
    private Building building;

    public Client() {
        super();
    }

    public Client(String name, String surname, String email, String password, Building building, LocalDate birthDate, String cellphone) {
        super(name,surname,email,password, "CLIENT", birthDate, cellphone);
        this.building = building;
    }

    @JsonProperty("buildingId") // lo vedrai nel JSON come "buildingId"
    public Long getBuildingId() {
        return (building != null) ? building.getId() : null;
    }


}
