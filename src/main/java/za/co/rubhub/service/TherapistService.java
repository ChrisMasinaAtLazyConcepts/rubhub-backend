package za.co.rubhub.service;

import za.co.rubhub.model.Therapist;
import za.co.rubhub.repositories.TherapistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TherapistService {

    @Autowired
    private TherapistRepository therapistRepository;

    
     public Optional<Therapist> findById(Long id) {
        return therapistRepository.findById(id);
    }

    public List<Therapist> findAll() {
        return therapistRepository.findAll();
    }

    public Optional<Therapist> findByTherapistId(Long therapistId) {
        return therapistRepository.findByTherapistId(therapistId);
    }

    public Optional<Therapist> findByEmail(String email) {
        return therapistRepository.findByEmail(email);
    }

    public List<Therapist> findBySpecialization(String specialization) {
        return therapistRepository.findBySpecializationsContaining(specialization);
    }

    public List<Therapist> findByVerificationStatus(String verificationStatus) {
        return therapistRepository.findByVerificationStatus(verificationStatus);
    }

    public List<Therapist> findActiveVerifiedTherapists() {
        return therapistRepository.findAvailableVerifiedTherapists();
    }

    public List<Therapist> findByLocationNear(double longitude, double latitude, double maxDistance) {
        return therapistRepository.findByLocationNear(longitude, latitude, maxDistance);
    }

    public List<Therapist> findByService(String service) {
        return therapistRepository.findByServiceType(service);
    }

    public Therapist save(Therapist therapist) {
        // Generate therapist ID if not provided
        if (therapist.getId() == null ) {
        }
        return therapistRepository.save(therapist);
    }


    public Therapist verifyTherapist(Long therapistId) {
        Optional<Therapist> therapistOpt = therapistRepository.findByTherapistId(therapistId);
        if (therapistOpt.isPresent()) {
            Therapist therapist = therapistOpt.get();
            therapist.verifyTherapist();
            return therapistRepository.save(therapist);
        }
        throw new RuntimeException("Therapist not found with ID: " + therapistId);
    }

    public Therapist rejectTherapist(Long therapistId, String reason) {
        Optional<Therapist> therapistOpt = therapistRepository.findByTherapistId(therapistId);
        if (therapistOpt.isPresent()) {
            Therapist therapist = therapistOpt.get();
            therapist.rejectTherapist(reason);
            return therapistRepository.save(therapist);
        }
        throw new RuntimeException("Therapist not found with ID: " + therapistId);
    }

    public Therapist updateRating(Long therapistId, Double newRating) {
        Optional<Therapist> therapistOpt = therapistRepository.findByTherapistId(therapistId);
        if (therapistOpt.isPresent()) {
            Therapist therapist = therapistOpt.get();
            // therapist.updateRating(newRating);
            return therapistRepository.save(therapist);
        }
        throw new RuntimeException("Therapist not found with ID: " );
    }

	public void incrementCompletedSessions(Long therapistId) {
		Optional<Therapist> therapistOpt = findByTherapistId(therapistId);
		if (therapistOpt.isPresent()) {
			Therapist therapist = therapistOpt.get();
			therapist.setCompletedSessions(therapist.getCompletedSessions() + 1);
			therapistRepository.save(therapist);
		}
	}

	public boolean isAvailableForBooking(Long therapistId) {
		Optional<Therapist> therapistOpt = findByTherapistId(therapistId);
		return therapistOpt.map(t -> t.isAvailableForBooking()).orElse(false);
	}

    public List<Therapist> findByAvailability(Boolean available) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByAvailability'");
    }
}