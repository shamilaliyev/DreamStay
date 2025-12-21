package models;

import java.util.List;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BUYER")
public class Buyer extends User {

    // JPA requires a no-arg constructor
    protected Buyer() {
        super();
    }

    public Buyer(Long id, String name, String email, String password, boolean isVerified, String governmentId) {
        super(id, name, email, "buyer", password, isVerified, governmentId);
    }

    // UI removed
}
