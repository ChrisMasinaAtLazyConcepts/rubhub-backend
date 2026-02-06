package za.co.rubhub.controller;

import za.co.rubhub.model.MassageServiceType;
import za.co.rubhub.service.MassageServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.validation.Valid;

/**
 * REST Controller for managing Massage Service Types
 */
@RestController
@RequestMapping("/api/massage-service-types")
@CrossOrigin(origins = "*") // Adjust origins as needed for security
public class MassageServiceTypeController {

    private final MassageServiceTypeService massageServiceTypeService;

    @Autowired
    public MassageServiceTypeController(MassageServiceTypeService massageServiceTypeService) {
        this.massageServiceTypeService = massageServiceTypeService;
    }

    /**
     * GET /api/massage-service-types - Get all massage service types
     */
    @GetMapping
    public ResponseEntity<List<MassageServiceType>> getAllMassageServiceTypes() {
        List<MassageServiceType> serviceTypes = massageServiceTypeService.getAllServiceTypes();
        return ResponseEntity.ok(serviceTypes);
    }

    /**
     * GET /api/massage-service-types/{id} - Get massage service type by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MassageServiceType> getMassageServiceTypeById(@PathVariable Long id) {
        return massageServiceTypeService.getServiceTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/massage-service-types/code/{code} - Get massage service type by code
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<MassageServiceType> getMassageServiceTypeByCode(@PathVariable String code) {
        return massageServiceTypeService.getServiceTypeByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/massage-service-types - Create a new massage service type
     */
    @PostMapping
    public ResponseEntity<MassageServiceType> createMassageServiceType(
            @Valid @RequestBody MassageServiceType massageServiceType) {
        MassageServiceType createdServiceType = massageServiceTypeService.createServiceType(massageServiceType);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdServiceType);
    }

    /**
     * PUT /api/massage-service-types/{id} - Update an existing massage service type
     */
    @PutMapping("/{id}")
    public ResponseEntity<MassageServiceType> updateMassageServiceType(
            @PathVariable Long id,
            @Valid @RequestBody MassageServiceType massageServiceType) {
        try {
            MassageServiceType updatedServiceType = massageServiceTypeService.updateServiceType(id, massageServiceType);
            return ResponseEntity.ok(updatedServiceType);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/massage-service-types/{id} - Delete a massage service type
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMassageServiceType(@PathVariable Long id) {
        try {
            massageServiceTypeService.deleteServiceType(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/massage-service-types/active - Get all active massage service types
     */
    @GetMapping("/active")
    public ResponseEntity<List<MassageServiceType>> getActiveMassageServiceTypes() {
        List<MassageServiceType> activeServiceTypes = massageServiceTypeService.getActiveServiceTypes();
        return ResponseEntity.ok(activeServiceTypes);
    }

    /**
     * PATCH /api/massage-service-types/{id}/status - Update status of a massage service type
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<MassageServiceType> updateServiceTypeStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        try {
            MassageServiceType updatedServiceType = massageServiceTypeService.updateServiceTypeStatus(id, active);
            return ResponseEntity.ok(updatedServiceType);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/massage-service-types/search - Search massage service types by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<MassageServiceType>> searchMassageServiceTypes(
            @RequestParam String name) {
        List<MassageServiceType> serviceTypes = massageServiceTypeService.searchServiceTypesByName(name);
        return ResponseEntity.ok(serviceTypes);
    }
}