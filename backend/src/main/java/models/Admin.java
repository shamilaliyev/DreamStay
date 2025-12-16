package models;

import java.util.List;
import services.MailService;

public class Admin extends User {
    // Admin uses AuthServer/PropertyManager/VerificationService injected in User

    public Admin(Long id, String name, String email, String password, boolean isVerified, String governmentId) {
        super(id, name, email, "admin", password, isVerified, governmentId);
    }
}
