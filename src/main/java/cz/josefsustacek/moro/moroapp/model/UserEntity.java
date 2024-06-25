package cz.josefsustacek.moro.moroapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity(name="users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @SequenceGenerator(
            name = "users_seq",
            initialValue = 50   // we have some data put into a clean schema, so make
                                // sure they have some space for their ids (1, 2, ...)
    )
    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Size(min = 1, max = 64)
    @Pattern(regexp = "[a-zA-Z0-9_\\.-]+")
    @Column(nullable = false, unique = true)
    private String username;

    // we'll use sha1
    @NotNull
    @Size(min = 1, max = 255)
    private String passwordHash;


    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }

    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

}