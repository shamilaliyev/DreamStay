package models;

import java.util.List;
import services.MailService;
import models.Property;

public class Seller extends User {

    public Seller(Long id, String name, String email, String password, boolean isVerified, String governmentId) {
        super(id, name, email, "seller", password, isVerified, governmentId);
    }
}
