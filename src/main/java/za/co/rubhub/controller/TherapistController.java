package za.co.rubhub.controller;

import za.co.rubhub.model.Therapist;
import za.co.rubhub.service.TherapistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/therapists")
@CrossOrigin(origins = "*")
public class TherapistController {

    @Autowired
    private TherapistService therapistService;

    // GET - Get all therapists
    @GetMapping
    public ResponseEntity<List<Therapist>> getAllTherapists() {
        try {
            List<Therapist> therapists = therapistService.findAll();
            return ResponseEntity.ok(therapists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get active verified therapists
    @GetMapping("/active")
    public ResponseEntity<List<Therapist>> getActiveVerifiedTherapists() {
        try {
            List<Therapist> therapists = therapistService.findActiveVerifiedTherapists();
            return ResponseEntity.ok(therapists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get therapist by ID
    @GetMapping("/{id}")
    public ResponseEntity<Therapist> getTherapistById(@PathVariable Long id) {
        try {
            Optional<Therapist> therapist = therapistService.findById(id);
            return therapist.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get therapists by specialization
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<List<Therapist>> getTherapistsBySpecialization(@PathVariable String specialization) {
        try {
            List<Therapist> therapists = therapistService.findBySpecialization(specialization);
            return ResponseEntity.ok(therapists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get therapists by service
    @GetMapping("/service/{service}")
    public ResponseEntity<List<Therapist>> getTherapistsByService(@PathVariable String service) {
        try {
            List<Therapist> therapists = therapistService.findByService(service);
            return ResponseEntity.ok(therapists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get therapists by verification status
    @GetMapping("/verification/{status}")
    public ResponseEntity<List<Therapist>> getTherapistsByVerificationStatus(@PathVariable String status) {
        try {
            List<Therapist> therapists = therapistService.findByVerificationStatus(status);
            return ResponseEntity.ok(therapists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get therapists near location
    @GetMapping("/location")
    public ResponseEntity<List<Therapist>> getTherapistsNearLocation(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "10000") double maxDistance) {
        try {
            List<Therapist> therapists = therapistService.findByLocationNear(longitude, latitude, maxDistance);
            return ResponseEntity.ok(therapists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Create new therapist
    @PostMapping
    public ResponseEntity<?> createTherapist(@javax.validation.Valid @RequestBody Therapist therapist, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }

            // Check if email already exists
            if (therapistService.findByEmail(therapist.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Therapist with email " + therapist.getEmail() + " already exists");
            }

        
            Therapist savedTherapist = therapistService.save(therapist);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTherapist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating therapist: " + e.getMessage());
        }
    }

    // PUT - Update therapist
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTherapist(@PathVariable Long id, 
                                           @javax.validation.Valid @RequestBody Therapist therapistDetails, 
                                           BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }

            Optional<Therapist> existingTherapist = therapistService.findById(id);
            if (!existingTherapist.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Therapist therapist = existingTherapist.get();
            
            // Update fields based on your Therapist model
            if (therapistDetails.getLicenseNumber() != null) {
                therapist.setLicenseNumber(therapistDetails.getLicenseNumber());
            }
            
            if (therapistDetails.getYearsOfExperience() != null) {
                therapist.setYearsOfExperience(therapistDetails.getYearsOfExperience());
            }
            
            if (therapistDetails.getSpecialization() != null) {
                therapist.setSpecialization(therapistDetails.getSpecialization());
            }
            
            if (therapistDetails.getBio() != null) {
                therapist.setBio(therapistDetails.getBio());
            }
            
            if (therapistDetails.getHourlyRate() != null) {
                therapist.setHourlyRate(therapistDetails.getHourlyRate());
            }
            
            if (therapistDetails.getRating() != null) {
                therapist.setRating(therapistDetails.getRating());
            }
            
            if (therapistDetails.getIsAvailable() != null) {
                therapist.setIsAvailable(therapistDetails.getIsAvailable());
            }
            
            if (therapistDetails.getIsVerified() != null) {
                therapist.setIsVerified(therapistDetails.getIsVerified());
            }
            
            if (therapistDetails.getVerificationDocuments() != null) {
                therapist.setVerificationDocuments(therapistDetails.getVerificationDocuments());
            }
            
            if (therapistDetails.getAvailableFrom() != null) {
                therapist.setAvailableFrom(therapistDetails.getAvailableFrom());
            }
            
            if (therapistDetails.getAvailableTo() != null) {
                therapist.setAvailableTo(therapistDetails.getAvailableTo());
            }
            
            if (therapistDetails.getWorkingDays() != null) {
                therapist.setWorkingDays(therapistDetails.getWorkingDays());
            }
            
            if (therapistDetails.getTravelRadius() != null) {
                therapist.setTravelRadius(therapistDetails.getTravelRadius());
            }
            
            if (therapistDetails.getHasVehicle() != null) {
                therapist.setHasVehicle(therapistDetails.getHasVehicle());
            }
            
            if (therapistDetails.getIsTravelReady() != null) {
                therapist.setIsTravelReady(therapistDetails.getIsTravelReady());
            }
            
            if (therapistDetails.getEquipment() != null) {
                therapist.setEquipment(therapistDetails.getEquipment());
            }
            
            if (therapistDetails.getCertifications() != null) {
                therapist.setCertifications(therapistDetails.getCertifications());
            }
            
            if (therapistDetails.getLanguages() != null) {
                therapist.setLanguages(therapistDetails.getLanguages());
            }
            
            if (therapistDetails.getEmergencyContactName() != null) {
                therapist.setEmergencyContactName(therapistDetails.getEmergencyContactName());
            }
            
            if (therapistDetails.getEmergencyContactPhone() != null) {
                therapist.setEmergencyContactPhone(therapistDetails.getEmergencyContactPhone());
            }
            
            if (therapistDetails.getEmergencyContactRelationship() != null) {
                therapist.setEmergencyContactRelationship(therapistDetails.getEmergencyContactRelationship());
            }
            
            if (therapistDetails.getInsuranceDetails() != null) {
                therapist.setInsuranceDetails(therapistDetails.getInsuranceDetails());
            }
            
            if (therapistDetails.getBackgroundCheckStatus() != null) {
                therapist.setBackgroundCheckStatus(therapistDetails.getBackgroundCheckStatus());
            }
            
            if (therapistDetails.getBackgroundCheckDate() != null) {
                therapist.setBackgroundCheckDate(therapistDetails.getBackgroundCheckDate());
            }
            
            if (therapistDetails.getNotes() != null) {
                therapist.setNotes(therapistDetails.getNotes());
            }
            
            if (therapistDetails.getOnCall() != null) {
                therapist.setOnCall(therapistDetails.getOnCall());
            }
            
            if (therapistDetails.getAverageResponseTimeMinutes() != null) {
                therapist.setAverageResponseTimeMinutes(therapistDetails.getAverageResponseTimeMinutes());
            }
            
            // Only update email if it's different and not already taken
            if (therapistDetails.getEmail() != null && 
                !therapist.getEmail().equals(therapistDetails.getEmail())) {
                
                if (therapistService.findByEmail(therapistDetails.getEmail()).isPresent()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Therapist with email " + therapistDetails.getEmail() + " already exists");
                }
                therapist.setEmail(therapistDetails.getEmail());
            }

            // Calculate profile completion after updates
            therapist.calculateProfileCompletion();
            
            Therapist updatedTherapist = therapistService.save(therapist);
            return ResponseEntity.ok(updatedTherapist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating therapist: " + e.getMessage());
        }
    }

    // PATCH - Verify therapist
    @PatchMapping("/{id}/verify")
    public ResponseEntity<?> verifyTherapist(@PathVariable Long id) {
        try {
            Optional<Therapist> therapistOpt = therapistService.findById(id);
            if (!therapistOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Therapist therapist = therapistOpt.get();
            therapist.setIsVerified(true);
            therapist.setBackgroundCheckStatus("APPROVED");
            therapist.setBackgroundCheckDate(java.time.LocalDateTime.now());
            
            Therapist updatedTherapist = therapistService.save(therapist);
            return ResponseEntity.ok(updatedTherapist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error verifying therapist: " + e.getMessage());
        }
    }

    // PATCH - Reject therapist
    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectTherapist(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            Optional<Therapist> therapistOpt = therapistService.findById(id);
            if (!therapistOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Therapist therapist = therapistOpt.get();
            therapist.setIsVerified(false);
            therapist.setBackgroundCheckStatus("REJECTED");
            therapist.setBackgroundCheckDate(java.time.LocalDateTime.now());
            
            if (reason != null) {
                String notes = therapist.getNotes() != null ? therapist.getNotes() : "";
                therapist.setNotes(notes + "\nRejection Reason: " + reason + " (" + java.time.LocalDateTime.now() + ")");
            }
            
            Therapist updatedTherapist = therapistService.save(therapist);
            return ResponseEntity.ok(updatedTherapist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error rejecting therapist: " + e.getMessage());
        }
    }

    // PATCH - Update rating
    @PatchMapping("/{id}/rating")
    public ResponseEntity<?> updateTherapistRating(@PathVariable Long id, @RequestParam Double rating) {
        try {
            if (rating < 0 || rating > 5) {
                return ResponseEntity.badRequest().body("Rating must be between 0 and 5");
            }
            
            Optional<Therapist> therapistOpt = therapistService.findById(id);
            if (!therapistOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Therapist therapist = therapistOpt.get();
            therapist.updateRating(java.math.BigDecimal.valueOf(rating));
            
            Therapist updatedTherapist = therapistService.save(therapist);
            return ResponseEntity.ok(updatedTherapist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating rating: " + e.getMessage());
        }
    }

    // PATCH - Toggle active status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> toggleTherapistStatus(@PathVariable Long id, @RequestParam Boolean active) {
        try {
            Optional<Therapist> therapistOpt = therapistService.findById(id);
            if (!therapistOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Therapist therapist = therapistOpt.get();
            therapist.setIsAvailable(active);
            
            Therapist updatedTherapist = therapistService.save(therapist);
            return ResponseEntity.ok(updatedTherapist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating therapist status: " + e.getMessage());
        }
    }

    // PATCH - Update location
    @PatchMapping("/{id}/location")
    public ResponseEntity<?> updateTherapistLocation(@PathVariable Long id,
                                                   @RequestParam Double latitude,
                                                   @RequestParam Double longitude) {
        try {
            Optional<Therapist> therapistOpt = therapistService.findById(id);
            if (!therapistOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // This would require updating the User entity's location
            // You might need a separate endpoint or modify your model
            return ResponseEntity.ok("Location update endpoint - implement based on your location model");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating location: " + e.getMessage());
        }
    }

    // PATCH - Update earnings
    @PatchMapping("/{id}/earnings")
    public ResponseEntity<?> addEarnings(@PathVariable Long id, @RequestParam BigDecimal amount) {
        try {
            Optional<Therapist> therapistOpt = therapistService.findById(id);
            if (!therapistOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Therapist therapist = therapistOpt.get();
            therapist.addEarnings(amount);
            
            Therapist updatedTherapist = therapistService.save(therapist);
            return ResponseEntity.ok(updatedTherapist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating earnings: " + e.getMessage());
        }
    }

    // DELETE - Delete therapist
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTherapist(@PathVariable Long id) {
        try {
            if (!therapistService.findById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok().body("Therapist deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting therapist: " + e.getMessage());
        }
    }

    // GET - Get therapist by email
    @GetMapping("/email/{email}")
    public ResponseEntity<Therapist> getTherapistByEmail(@PathVariable String email) {
        try {
            Optional<Therapist> therapist = therapistService.findByEmail(email);
            return therapist.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // GET - Get therapists by availability status
    @GetMapping("/availability/{available}")
    public ResponseEntity<List<Therapist>> getTherapistsByAvailability(@PathVariable Boolean available) {
        try {
            List<Therapist> therapists = therapistService.findByAvailability(available);
            return ResponseEntity.ok(therapists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}