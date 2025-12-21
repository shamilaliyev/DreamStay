package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import models.Property;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {

    // Find all properties owned by a specific user
    List<Property> findByOwnerId(Long ownerId);

    // Find all unverified properties (for Admin)
    List<Property> findByIsVerifiedFalse();

    // Find all verified properties (for public search)
    List<Property> findByIsVerifiedTrue();
}
