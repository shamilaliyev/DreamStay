package models;

import java.util.List;
import services.MailService;

public class Buyer extends User {

    public Buyer(Long id, String name, String email, String password, boolean isVerified, String governmentId) {
        super(id, name, email, "buyer", password, isVerified, governmentId);
    }

    // UI removed
}
