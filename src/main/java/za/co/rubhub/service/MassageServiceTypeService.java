package za.co.rubhub.service;

import za.co.rubhub.model.MassageServiceType;
import java.util.List;
import java.util.Optional;

public interface MassageServiceTypeService {
    
    List<MassageServiceType> getAllServiceTypes();
    
    Optional<MassageServiceType> getServiceTypeById(Long id);
    
    Optional<MassageServiceType> getServiceTypeByCode(String code);
    
    MassageServiceType createServiceType(MassageServiceType massageServiceType);
    
    MassageServiceType updateServiceType(Long id, MassageServiceType massageServiceType);
    
    void deleteServiceType(Long id);
    
    List<MassageServiceType> getActiveServiceTypes();
    
    MassageServiceType updateServiceTypeStatus(Long id, boolean active);
    
    List<MassageServiceType> searchServiceTypesByName(String name);
}