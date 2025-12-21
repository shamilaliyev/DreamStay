package models;

import java.util.List;
import models.Property;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("AGENT")
public class Agent extends User {

    // JPA requires a no-arg constructor
    protected Agent() {
        super();
    }

    public Agent(Long id, String name, String email, String password, boolean isVerified, String governmentId) {
        super(id, name, email, "agent", password, isVerified, governmentId);
    }
}
