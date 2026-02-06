package za.co.rubhub.service.impl;

import za.co.rubhub.model.MassageServiceType;
import za.co.rubhub.repositories.MassageServiceTypeRepository;
import za.co.rubhub.service.MassageServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MassageServiceTypeServiceImpl implements MassageServiceTypeService {

    private final MassageServiceTypeRepository massageServiceTypeRepository;

    @Autowired
    public MassageServiceTypeServiceImpl(MassageServiceTypeRepository massageServiceTypeRepository) {
        this.massageServiceTypeRepository = massageServiceTypeRepository;
    }

    @Override
    public List<MassageServiceType> getAllServiceTypes() {
        return massageServiceTypeRepository.findAll();
    }

    @Override
    public Optional<MassageServiceType> getServiceTypeById(Long id) {
        return massageServiceTypeRepository.findById(id);
    }

    @Override
    public Optional<MassageServiceType> getServiceTypeByCode(String code) {
        return massageServiceTypeRepository.findByServiceCode(code);
    }

    @Override
    public MassageServiceType createServiceType(MassageServiceType massageServiceType) {
        // Set default values
        massageServiceType.setActive(true);
        
        // Check if code already exists
        if (massageServiceTypeRepository.findByName(massageServiceType.getName()) !=null) {
            throw new RuntimeException("Service already exists: " + massageServiceType.getName());
        }
        
        return massageServiceTypeRepository.save(massageServiceType);
    }

    @Override
    public MassageServiceType updateServiceType(Long id, MassageServiceType massageServiceType) {
        return massageServiceTypeRepository.findById(id)
                .map(existingServiceType -> {
                    // Update fields if provided
                    if (massageServiceType.getName() != null) {
                        existingServiceType.setName(massageServiceType.getName());
                    }
                    if (massageServiceType.getDescription() != null) {
                        existingServiceType.setDescription(massageServiceType.getDescription());
                    }
                    if (massageServiceType.getPrice() != null) {
                        existingServiceType.setPrice(massageServiceType.getPrice());
                    }
                    if (massageServiceType.getDuration() != null) {
                        existingServiceType.setDuration(massageServiceType.getDuration());
                    }
                    if (massageServiceType.getCategory() != null) {
                        existingServiceType.setCategory(massageServiceType.getCategory());
                    }
                    
                    return massageServiceTypeRepository.save(existingServiceType);
                })
                .orElseThrow(() -> new RuntimeException("MassageServiceType not found with id: " + id));
    }

    @Override
    public void deleteServiceType(Long id) {
        if (!massageServiceTypeRepository.existsById(id)) {
            throw new RuntimeException("MassageServiceType not found with id: " + id);
        }
        massageServiceTypeRepository.deleteById(id);
    }

    @Override
    public List<MassageServiceType> getActiveServiceTypes() {
        return massageServiceTypeRepository.findByActiveTrue();
    }

    @Override
    public MassageServiceType updateServiceTypeStatus(Long id, boolean active) {
        return massageServiceTypeRepository.findById(id)
                .map(serviceType -> {
                    serviceType.setActive(active);
                    return massageServiceTypeRepository.save(serviceType);
                })
                .orElseThrow(() -> new RuntimeException("MassageServiceType not found with id: " + id));
    }

    @Override
    public List<MassageServiceType> searchServiceTypesByName(String name) {
        return massageServiceTypeRepository.findByName(name);
    }
}