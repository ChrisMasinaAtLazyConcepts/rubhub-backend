package za.co.rubhub.service;

import za.co.rubhub.model.Location;
import za.co.rubhub.model.MassageRequest;
import za.co.rubhub.model.Therapist;
import za.co.rubhub.model.User;
import za.co.rubhub.model.MassageRequest.PaymentStatus;
import za.co.rubhub.model.MassageRequest.Status;
import za.co.rubhub.repositories.MassageRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MassageRequestService {

    @Autowired
    private MassageRequestRepository massageRequestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TherapistService therapistService;

    // Basic CRUD operations
    public List<MassageRequest> findAll() {
        return massageRequestRepository.findAll();
    }

    public Optional<MassageRequest> findById(Long id) {
        return massageRequestRepository.findById(id);
    }

    public List<MassageRequest> findByCustomerId(Long customerId) {
        return massageRequestRepository.findByCustomerId(customerId);
    }

    public List<MassageRequest> findByTherapistId(Long therapistId) {
        return massageRequestRepository.findByTherapistId(therapistId);
    }

    public List<MassageRequest> findByStatus(String status) {
        return massageRequestRepository.findByStatus(status);
    }

    public MassageRequest save(MassageRequest massageRequest) {
        // Calculate totals before saving
        massageRequest.calculateTotals();
        massageRequest.setUpdatedAt(LocalDateTime.now());
        
        return massageRequestRepository.save(massageRequest);
    }

    public void deleteById(String id) {
    }

    // Business logic methods
    public MassageRequest createMassageRequest(MassageRequest massageRequest) {
        // Validate customer exists
        Optional<User> customer = userService.findById(Long.parseLong(massageRequest.getCustomerId()));
        if (customer == null) {
            throw new RuntimeException("Customer not found with ID: " + massageRequest.getCustomerId());
        }
        massageRequest.setCustomer(customer.get());

        // Set initial status
        massageRequest.setStatus(Status.PENDING);
        massageRequest.setPaymentStatus(PaymentStatus.PENDING);

        return save(massageRequest);
    }

    public MassageRequest assignTherapist(Long requestId, Long therapistId) {
        Optional<MassageRequest> requestOpt = findById(requestId);
        Optional<Therapist> therapistOpt = therapistService.findByTherapistId(therapistId);

        if (requestOpt.isPresent() && therapistOpt.isPresent()) {
            MassageRequest request = requestOpt.get();
            Therapist therapist = therapistOpt.get();

            // Check for scheduling conflicts
            // if (hasSchedulingConflict(therapistId, request.getScheduledTime(), request.getDuration())) {
            //     throw new RuntimeException("Therapist has scheduling conflict at the requested time");
            // }

            request.assignToTherapist(therapist);
            return save(request);
        }
        throw new RuntimeException("Request or therapist not found");
    }



    public MassageRequest acceptRequest(Long requestId, String therapistId) {
        Optional<MassageRequest> requestOpt = findById(requestId);
        if (requestOpt.isPresent()) {
            MassageRequest request = requestOpt.get();
            
            // Verify therapist matches
            if (!request.getId().equals(therapistId)) {
                throw new RuntimeException("Therapist ID does not match request");
            }

            request.setStatus("ACCEPTED");
            return save(request);
        }
        throw new RuntimeException("Massage request not found with ID: " + requestId);
    }

    public MassageRequest startSession(Long requestId) {
        Optional<MassageRequest> requestOpt = findById(requestId);
        if (requestOpt.isPresent()) {
            MassageRequest request = requestOpt.get();
            
            if (request.getStatus().equals("ACCEPTED") && 
                request.getStatus().equals("PREPARATION")) {
                throw new RuntimeException("Request must be accepted or in preparation to start");
            }

            request.setStatus("IN_PROGRESS");
            return save(request);
        }
        throw new RuntimeException("Massage request not found with ID: " + requestId);
    }

    public MassageRequest cancelRequest(Long requestId, String reason) {
        Optional<MassageRequest> requestOpt = findById(requestId);
        if (requestOpt.isPresent()) {
            MassageRequest request = requestOpt.get();
            
            // Only allow cancellation if not completed
            if (request.getStatus().equals("COMPLETED")) {
                throw new RuntimeException("Cannot cancel completed request");
            }

            request.setStatus("CANCELLED");
            if (request.getSpecialRequests() == null) {
                request.setSpecialRequests("Cancelled: " + reason);
            } else {
                request.setSpecialRequests(request.getSpecialRequests() + "\nCancelled: " + reason);
            }
            
            return save(request);
        }
        throw new RuntimeException("Massage request not found with ID: " + requestId);
    }

    public MassageRequest addRating(Long requestId, Integer score, String review) {
        Optional<MassageRequest> requestOpt = findById(requestId);
        if (requestOpt.isPresent()) {
            MassageRequest request = requestOpt.get();
            
        

            // MassageRequest.Rating rating = new MassageRequest.Rating(score, review, LocalDateTime.now(), review, null, review, null);
            // request.setClientRating(rating);
            
            return save(request);
        }
        throw new RuntimeException("Massage request not found with ID: " + requestId);
    }

    // Search and filter methods
    public List<MassageRequest> findUnassignedRequests() {
        return massageRequestRepository.findUnassignedRequests();
    }

    public List<MassageRequest> findActiveRequests() {
        return massageRequestRepository.findActiveRequests();
    }

    public List<MassageRequest> findCustomerRequestsByStatus(String customerId, String status) {
        return massageRequestRepository.findByCustomerIdAndTherapistId(customerId, status);
    }

    public List<MassageRequest> findTherapistRequestsByStatus(String therapistId, String status) {
        return massageRequestRepository.findByTherapistIdAndStatus(therapistId, status);
    }

    public List<MassageRequest> findRequestsByDateRange(LocalDateTime start, LocalDateTime end) {
        return massageRequestRepository.findByScheduledTimeBetween(start, end);
    }

    public List<MassageRequest> findActivePanicSituations() {
        return massageRequestRepository.findActivePanicSituations();
    }

    public List<MassageRequest> findRequestsRequiringPayment() {
        return massageRequestRepository.findRequestsRequiringPayment();
    }

    // Utility methods
    private boolean hasSchedulingConflict(Long therapistId, LocalDateTime scheduledTime, Integer duration) {
        LocalDateTime endTime = scheduledTime.plusMinutes(duration);
        List<MassageRequest> conflicts = massageRequestRepository.findTherapistConflicts(therapistId, scheduledTime, endTime);
        return !conflicts.isEmpty();
    }

    public long getRequestCountByStatus(String status) {
        return massageRequestRepository.countByStatus(status);
    }

    public List<MassageRequest> searchRequests(String clientName, String clientPhone, String serviceType,
            String location) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchRequests'");
    }

    public long countAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'countAll'");
    }

    public long getRequestCountByTherapistAndStatus(Long therapistId, String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRequestCountByTherapistAndStatus'");
    }

    public long getRequestCountByTherapist(Long therapistId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRequestCountByTherapist'");
    }


    public long getRequestCountByCustomer(String customerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRequestCountByCustomer'");
    }

    public MassageRequest updateStatus(Long id, String status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateStatus'");
    }

    public MassageRequest updatePaymentStatus(Long id, String paymentStatus) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePaymentStatus'");
    }

    public MassageRequest completeSession(Long id, BigDecimal actualPrice, Integer rating, String feedback) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'completeSession'");
    }

    public MassageRequest rejectRequest(Long id, String reason) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'rejectRequest'");
    }

    public List<MassageRequest> findRequestsWithCompletedPayment() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findRequestsWithCompletedPayment'");
    }

    public List<MassageRequest> findCustomerRequestsByStatus(Long customerId, String status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findCustomerRequestsByStatus'");
    }

    public List<MassageRequest> findTherapistRequestsByStatus(Long therapistId, String status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findTherapistRequestsByStatus'");
    }

    public long getRequestCountByCustomer(Long customerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRequestCountByCustomer'");
    }

  
}