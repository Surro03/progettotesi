package it.univaq.progettotesi.entity;

import jakarta.persistence.*;


import java.io.Serial;
import java.io.Serializable;

@Entity
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    protected User() {
        // no-args constructor required by JPA spec
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    //Getter e Setter
    public Long getId() { return this.id;}
    public String getUsername() { return username;}
    public String getEmail() { return this.email; }
    public String getRole() { return this.role; }
    //public String getPassword() { return this.password; }


    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(String Role) { this.role = role; }




}
