package it.univaq.progettotesi.entity;


import jakarta.persistence.*;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="buildings")
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @ManyToOne(optional=false)
    @JoinColumn(name = "user_id")
    @Getter
    private User user;

    @Column(nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(nullable=false)
    @Getter
    @Setter
    private String address;

    //@Column(nullable=false)
    //private String ifcIdentifier;

    public Building() {
        // no-args constructor richiesto da JPA
    }

    public Building(User user, String name, String address) {
        this.user = user;
        this.name = name;
        this.address = address;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Building)) return false;

        return this.id != null && this.id.equals(((Building) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.user.getId());
    }

    @Override
    public String toString() {
        return "Building{" +
                "id=" + this.id +
                ", user= " + this.user +
                ", name= " + this.name +
                ", address= "+ this.address + '}';
    }


}
