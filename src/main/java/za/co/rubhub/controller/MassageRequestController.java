package za.co.rubhub.controller;

import za.co.rubhub.model.MassageRequest;
import za.co.rubhub.service.MassageRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import java.util.HashMap;

@RestController
@RequestMapping("/api/massage-requests")
@CrossOrigin(origins = "*")
public class MassageRequestController {

    @Autowired
    private MassageRequestService massageRequestService;

    // GET - Get all massage requests
    @GetMapping
    public ResponseEntity<List<MassageRequest>> getAllMassageRequests(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long therapistId) {
        try {
            List<MassageRequest> requests;
            
            if (status != null && customerId != null) {
                requests = massageRequestService.findCustomerRequestsByStatus(customerId, status);
            } else if (status != null && therapistId != null) {
                requests = massageRequestService.findTherapistRequestsByStatus(therapistId, status);
            } else if (status != null) {
                requests = massageRequestService.findByStatus(status);
            } else if (customerId != null) {
                requests = massageRequestService.findByCustomerId(customerId);
            } else if (therapistId != null) {
                requests = massageRequestService.findByTherapistId(therapistId);
            } else {
                requests = massageRequestService.findAll();
            }
            
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get active massage requests
    @GetMapping("/active")
    public ResponseEntity<List<MassageRequest>> getActiveMassageRequests() {
        try {
            List<MassageRequest> requests = massageRequestService.findActiveRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get unassigned massage requests
    @GetMapping("/unassigned")
    public ResponseEntity<List<MassageRequest>> getUnassignedMassageRequests() {
        try {
            List<MassageRequest> requests = massageRequestService.findUnassignedRequests();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get massage request by ID
    @GetMapping("/{id}")
    public ResponseEntity<MassageRequest> getMassageRequestById(@PathVariable Long id) {
        try {
            Optional<MassageRequest> request = massageRequestService.findById(id);
            return request.map(ResponseEntity::ok)
                         .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get massage requests by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<MassageRequest>> getMassageRequestsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            List<MassageRequest> requests = massageRequestService.findRequestsByDateRange(start, end);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // GET - Get massage requests requiring payment
    @GetMapping("/payment-pending")
    public ResponseEntity<List<MassageRequest>> getRequestsRequiringPayment() {
        try {
            List<MassageRequest> requests = massageRequestService.findRequestsRequiringPayment();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get massage requests with completed payment
    @GetMapping("/payment-completed")
    public ResponseEntity<List<MassageRequest>> getRequestsWithCompletedPayment() {
        try {
            List<MassageRequest> requests = massageRequestService.findRequestsWithCompletedPayment();
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Create new massage request
    @PostMapping
    public ResponseEntity<?> createMassageRequest(@Valid @RequestBody MassageRequest massageRequest, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }

            MassageRequest savedRequest = massageRequestService.createMassageRequest(massageRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating massage request: " + e.getMessage());
        }
    }

    // PUT - Update massage request
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMassageRequest(@PathVariable Long id, 
                                                 @Valid @RequestBody MassageRequest requestDetails, 
                                                 BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }

            Optional<MassageRequest> existingRequest = massageRequestService.findById(id);
            if (!existingRequest.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            MassageRequest request = existingRequest.get();
            
            // Update allowed fields based on your MassageRequest model
            if (requestDetails.getClientName() != null) {
                request.setClientName(requestDetails.getClientName());
            }
            
            if (requestDetails.getClientPhone() != null) {
                request.setClientPhone(requestDetails.getClientPhone());
            }
            
            if (requestDetails.getClientEmail() != null) {
                request.setClientEmail(requestDetails.getClientEmail());
            }
            
            if (requestDetails.getServiceType() != null) {
                request.setServiceType(requestDetails.getServiceType());
            }
            
            if (requestDetails.getDurationMinutes() != null) {
                request.setDurationMinutes(requestDetails.getDurationMinutes());
            }
            
            if (requestDetails.getPreferredDateTime() != null) {
                request.setPreferredDateTime(requestDetails.getPreferredDateTime());
            }
            
            if (requestDetails.getLocationAddress() != null) {
                request.setLocationAddress(requestDetails.getLocationAddress());
            }
            
            if (requestDetails.getLocationLatitude() != null) {
                request.setLocationLatitude(requestDetails.getLocationLatitude());
            }
            
            if (requestDetails.getLocationLongitude() != null) {
                request.setLocationLongitude(requestDetails.getLocationLongitude());
            }
            
            if (requestDetails.getSpecialInstructions() != null) {
                request.setSpecialInstructions(requestDetails.getSpecialInstructions());
            }
            
            if (requestDetails.getUrgencyLevel() != null) {
                request.setUrgencyLevel(requestDetails.getUrgencyLevel());
            }
            
            if (requestDetails.getEstimatedPrice() != null) {
                request.setEstimatedPrice(requestDetails.getEstimatedPrice());
            }
            
            if (requestDetails.getActualPrice() != null) {
                request.setActualPrice(requestDetails.getActualPrice());
            }
            
            if (requestDetails.getPaymentStatus() != null) {
                request.setPaymentStatus(requestDetails.getPaymentStatus());
            }
            
            if (requestDetails.getClient() != null) {
                request.setClientRatingScore(requestDetails.getRatingStars());
            }
            
            if (requestDetails.getClientFeedback() != null) {
                request.setClientFeedback(requestDetails.getClientFeedback());
            }
            
            if (requestDetails.getCancellationReason() != null) {
                request.setCancellationReason(requestDetails.getCancellationReason());
            }

            MassageRequest updatedRequest = massageRequestService.save(request);
            return ResponseEntity.ok(updatedRequest);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating massage request: " + e.getMessage());
        }
    }

    // PATCH - Assign therapist to request
    @PatchMapping("/{id}/assign-therapist")
    public ResponseEntity<?> assignTherapist(@PathVariable Long id, @RequestParam Long therapistId) {
        try {
            MassageRequest request = massageRequestService.assignTherapist(id, therapistId);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error assigning therapist: " + e.getMessage());
        }
    }

    // PATCH - Accept request (by therapist)
    @PatchMapping("/{id}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable Long id, @PathVariable String therapisId) {
        try {
            MassageRequest request = massageRequestService.acceptRequest(id, therapisId);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error accepting request: " + e.getMessage());
        }
    }

    // PATCH - Reject request
    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            MassageRequest request = massageRequestService.rejectRequest(id, reason);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error rejecting request: " + e.getMessage());
        }
    }

    // PATCH - Start session
    @PatchMapping("/{id}/start")
    public ResponseEntity<?> startSession(@PathVariable Long id) {
        try {
            MassageRequest request = massageRequestService.startSession(id);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error starting session: " + e.getMessage());
        }
    }

    // PATCH - Complete session
    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completeSession(@PathVariable Long id,
                                            @RequestParam(required = false) BigDecimal actualPrice,
                                            @RequestParam(required = false) Integer rating,
                                            @RequestParam(required = false) String feedback) {
        try {
            MassageRequest request = massageRequestService.completeSession(id, actualPrice, rating, feedback);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error completing session: " + e.getMessage());
        }
    }

    // PATCH - Cancel request
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelRequest(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            MassageRequest request = massageRequestService.cancelRequest(id, reason);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error cancelling request: " + e.getMessage());
        }
    }

    // PATCH - Add rating to request
    @PatchMapping("/{id}/rating")
    public ResponseEntity<?> addRating(@PathVariable Long id,
                                      @RequestParam Integer score,
                                      @RequestParam(required = false) String review) {
        try {
            MassageRequest request = massageRequestService.addRating(id, score, review);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error adding rating: " + e.getMessage());
        }
    }

    // PATCH - Update payment status
    @PatchMapping("/{id}/payment")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long id,
                                               @RequestParam String paymentStatus) {
        try {
            MassageRequest request = massageRequestService.updatePaymentStatus(id, paymentStatus);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating payment status: " + e.getMessage());
        }
    }

    // PATCH - Update status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                         @RequestParam String status) {
        try {
            MassageRequest request = massageRequestService.updateStatus(id, status);
            return ResponseEntity.ok(request);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating status: " + e.getMessage());
        }
    }

    // GET - Massage request statistics
    @GetMapping("/statistics")
    public ResponseEntity<?> getMassageRequestStatistics(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long therapistId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Map<String, Object> statistics = new HashMap<>();
            
            if (customerId != null) {
                long count = massageRequestService.getRequestCountByCustomer(customerId);
                statistics.put("totalRequests", count);
                
                // Add more customer-specific stats
                long completed = massageRequestService.getRequestCountByTherapistAndStatus(customerId, "COMPLETED");
                long pending = massageRequestService.getRequestCountByTherapistAndStatus(customerId, "PENDING");
                statistics.put("completedRequests", completed);
                statistics.put("pendingRequests", pending);
                
            } else if (therapistId != null) {
                long count = massageRequestService.getRequestCountByTherapist(therapistId);
                statistics.put("totalRequests", count);
                
                // Add more therapist-specific stats
                long completed = massageRequestService.getRequestCountByTherapistAndStatus(therapistId, "COMPLETED");
                long accepted = massageRequestService.getRequestCountByTherapistAndStatus(therapistId, "ACCEPTED");
                statistics.put("completedRequests", completed);
                statistics.put("acceptedRequests", accepted);
                
            } else {
                // Overall statistics
                long total = massageRequestService.countAll();
                long pending = massageRequestService.getRequestCountByCustomer("PENDING");
                long assigned = massageRequestService.getRequestCountByStatus("ASSIGNED");
                long accepted = massageRequestService.getRequestCountByCustomer("ACCEPTED");
                long completed = massageRequestService.getRequestCountByCustomer("COMPLETED");
                long cancelled = massageRequestService.getRequestCountByStatus("CANCELLED");
                long rejected = massageRequestService.getRequestCountByCustomer("REJECTED");
                
                statistics.put("total", total);
                statistics.put("pending", pending);
                statistics.put("assigned", assigned);
                statistics.put("accepted", accepted);
                statistics.put("completed", completed);
                statistics.put("cancelled", cancelled);
                statistics.put("rejected", rejected);
                
                // Calculate percentages
                if (total > 0) {
                    statistics.put("completionRate", (double) completed / total * 100);
                    statistics.put("cancellationRate", (double) cancelled / total * 100);
                }
            }
            
            // Date range statistics if provided
            if (startDate != null && endDate != null) {
                List<MassageRequest> dateRangeRequests = massageRequestService.findRequestsByDateRange(startDate, endDate);
                statistics.put("dateRangeTotal", dateRangeRequests.size());
                
                long dateRangeCompleted = dateRangeRequests.stream()
                    .filter(r -> "COMPLETED".equals(r.getStatus()))
                    .count();
                statistics.put("dateRangeCompleted", dateRangeCompleted);
            }
            
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving statistics: " + e.getMessage());
        }
    }

    // GET - Get revenue statistics
    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenueStatistics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) Long therapistId) {
        try {
            Map<String, Object> revenueStats = new HashMap<>();
            
            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal pendingRevenue = BigDecimal.ZERO;
            BigDecimal completedRevenue = BigDecimal.ZERO;
            
            List<MassageRequest> requests;
            if (startDate != null && endDate != null) {
                requests = massageRequestService.findRequestsByDateRange(startDate, endDate);
            } else {
                requests = massageRequestService.findAll();
            }
            
                            // Filter by therapist if specified

                // In your getRevenueStatistics method:
                if (therapistId != null) {
                    requests = requests.stream()
                        .filter(r -> r.getTherapist() != null && therapistId.equals(r.getTherapist().getId()))
                        .collect(Collectors.toList()); // Changed from .toList() to .collect(Collectors.toList())
                }
            
            for (MassageRequest request : requests) {
                BigDecimal price = request.getActualPrice() != null ? request.getActualPrice() : 
                                  request.getEstimatedPrice() != null ? request.getEstimatedPrice() : BigDecimal.ZERO;
                
                totalRevenue = totalRevenue.add(price);
                
                if ("COMPLETED".equals(request.getStatus()) && "PAID".equals(request.getPaymentStatus())) {
                    completedRevenue = completedRevenue.add(price);
                } else if ("PENDING".equals(request.getPaymentStatus())) {
                    pendingRevenue = pendingRevenue.add(price);
                }
            }
            
            revenueStats.put("totalRevenue", totalRevenue);
            revenueStats.put("completedRevenue", completedRevenue);
            revenueStats.put("pendingRevenue", pendingRevenue);
            revenueStats.put("requestCount", requests.size());
            
            return ResponseEntity.ok(revenueStats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving revenue statistics: " + e.getMessage());
        }
    }

    // DELETE - Delete massage request
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMassageRequest(@PathVariable Long id) {
        try {
            if (!massageRequestService.findById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok().body("Massage request deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting massage request: " + e.getMessage());
        }
    }

    // GET - Search massage requests
    @GetMapping("/search")
    public ResponseEntity<List<MassageRequest>> searchMassageRequests(
            @RequestParam(required = false) String clientName,
            @RequestParam(required = false) String clientPhone,
            @RequestParam(required = false) String serviceType,
            @RequestParam(required = false) String location) {
        try {
            List<MassageRequest> requests = massageRequestService.searchRequests(clientName, clientPhone, serviceType, location);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}