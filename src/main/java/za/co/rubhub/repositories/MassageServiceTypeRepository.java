package za.co.rubhub.repositories;

import za.co.rubhub.model.MassageServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MassageServiceTypeRepository extends JpaRepository<MassageServiceType, Long> {
    
    Optional<MassageServiceType> findByServiceCode(String serviceCode);
    
    List<MassageServiceType> findByActiveTrue();
    
    List<MassageServiceType> findByName(String serviceName);
    
    boolean existsByServiceCode(String serviceCode);
}