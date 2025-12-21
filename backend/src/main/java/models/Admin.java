package models;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {
    // Admin uses AuthServer/PropertyManager/VerificationService injected in User

    // JPA requires a no-arg constructor
    protected Admin() {
        super();
    }

    public Admin(Long id, String name, String email, String password, boolean isVerified, String governmentId) {
        super(id, name, email, "admin", password, isVerified, governmentId);
    }
}
