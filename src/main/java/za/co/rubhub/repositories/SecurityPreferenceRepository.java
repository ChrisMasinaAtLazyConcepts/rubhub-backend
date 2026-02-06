package za.co.rubhub.repositories;

import za.co.rubhub.model.SecurityPreference;
import za.co.rubhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityPreferenceRepository extends JpaRepository<SecurityPreference, Long> {
    Optional<SecurityPreference> findByUser(User user);
    Optional<SecurityPreference> findByUserId(Long userId);
    boolean existsByUser(User user);
}