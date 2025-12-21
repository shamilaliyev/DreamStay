package models;

import java.util.List;
import models.Property;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SELLER")
public class Seller extends User {

    // JPA requires a no-arg constructor
    protected Seller() {
        super();
    }

    public Seller(Long id, String name, String email, String password, boolean isVerified, String governmentId) {
        super(id, name, email, "seller", password, isVerified, governmentId);
    }
}
