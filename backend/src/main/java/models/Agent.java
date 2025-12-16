package models;

import java.util.List;
import services.MailService;
import models.Property;

public class Agent extends User {

    public Agent(Long id, String name, String email, String password, boolean isVerified, String governmentId) {
        super(id, name, email, "agent", password, isVerified, governmentId);
    }
}
