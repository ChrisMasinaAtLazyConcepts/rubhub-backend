package za.co.rubhub.controller;

import za.co.rubhub.model.Booking;
import za.co.rubhub.model.BookingStatus;
import za.co.rubhub.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // GET - Get all bookings
    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings(
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String therapistId) {
        try {
            List<Booking> bookings;
            
            if (status != null && customerId != null) {
                bookings = bookingService.findCustomerBookingsByStatus(customerId, status);
            } else if (status != null && therapistId != null) {
                bookings = bookingService.findTherapistBookingsByStatus(therapistId, status);
            } else if (status != null) {
                bookings = bookingService.findByStatus(status);
            } else if (customerId != null) {
                bookings = bookingService.findByCustomerId(customerId);
            } else if (therapistId != null) {
                bookings = bookingService.findByTherapistId(therapistId);
            } else {
                bookings = bookingService.findAll();
            }
            
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get active bookings
    @GetMapping("/active")
    public ResponseEntity<List<Booking>> getActiveBookings() {
        try {
            List<Booking> bookings = bookingService.findActiveBookings();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get booking by ID
    @GetMapping("/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable Long id) {
        try {
            Optional<Booking> booking = bookingService.findById(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get booking by request ID
    @GetMapping("/request/{requestId}")
    public ResponseEntity<Booking> getBookingByRequestId(@PathVariable String requestId) {
        try {
            Booking booking = bookingService.findByRequestId(requestId);
            return ResponseEntity(HttpStatus.OK, booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Booking> ResponseEntity(HttpStatus ok, Booking booking) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ResponseEntity'");
    }

    // GET - Get bookings by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<Booking>> getBookingsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            List<Booking> bookings = bookingService.findBookingsByDateRange(start, end);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET - Get bookings ready for payout
    @GetMapping("/payout/ready")
    public ResponseEntity<List<Booking>> getBookingsReadyForPayout() {
        try {
            List<Booking> bookings = bookingService.findBookingsReadyForPayout();
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST - Create new booking
    @PostMapping
    public ResponseEntity<?> createBooking(@Valid @RequestBody Booking booking, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }

            Booking savedBooking = bookingService.createBooking(booking);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating booking: " + e.getMessage());
        }
    }

    // PUT - Update booking
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @Valid @RequestBody Booking bookingDetails, BindingResult result) {
        try {
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getAllErrors());
            }

            Optional<Booking> existingBooking = bookingService.findById(id);
            if (existingBooking == null) {
                return ResponseEntity.notFound().build();
            }

            Booking booking = existingBooking.get();
            
            // Update allowed fields
            booking.setServiceType(bookingDetails.getServiceType());
            booking.setServiceType(bookingDetails.getServiceType());
            booking.setPrice(bookingDetails.getPrice());
            booking.setTravelFee(bookingDetails.getTravelFee());
            booking.setDiscount(bookingDetails.getDiscount());
            booking.setSpecialRequests(bookingDetails.getSpecialRequests());
            booking.setAddress(bookingDetails.getAddress());
            booking.setScheduledTime(bookingDetails.getScheduledTime());
            booking.setPreparationTime(bookingDetails.getPreparationTime());

            Booking updatedBooking = bookingService.save(booking);
            return ResponseEntity.ok(updatedBooking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating booking: " + e.getMessage());
        }
    }

    // PATCH - Accept booking (by therapist)
    @PatchMapping("/{id}/accept")
    public ResponseEntity<?> acceptBooking(@PathVariable String id, @RequestParam String therapistId) {
        try {
            Booking booking = bookingService.acceptBooking(id, therapistId);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error accepting booking: " + e.getMessage());
        }
    }

    // PATCH - Reject booking (by therapist)
    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectBooking(@PathVariable String id, 
                                         @RequestParam String therapistId,
                                         @RequestParam String reason) {
        try {
            Booking booking = bookingService.rejectBooking(id, therapistId, reason);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error rejecting booking: " + e.getMessage());
        }
    }

    // PATCH - Start booking
    @PatchMapping("/{id}/start")
    public ResponseEntity<?> startBooking(@PathVariable String id) {
        try {
            Booking booking = bookingService.startBooking(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error starting booking: " + e.getMessage());
        }
    }

    // PATCH - Complete booking
    @PatchMapping("/{id}/complete")
    public ResponseEntity<?> completeBooking(@PathVariable String id) {
        try {
            Booking booking = bookingService.completeBooking(id);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error completing booking: " + e.getMessage());
        }
    }

    // PATCH - Cancel booking
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String id, @RequestParam String reason) {
        try {
            Booking booking = bookingService.cancelBooking(id, reason);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error cancelling booking: " + e.getMessage());
        }
    }

    // PATCH - Update payment status
    @PatchMapping("/{id}/payment")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long id,
                                               @RequestParam String paymentStatus,
                                               @RequestParam(required = false) String transactionId) {
        try {
            Booking booking = bookingService.updatePaymentStatus(id, paymentStatus, transactionId);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating payment status: " + e.getMessage());
        }
    }

    // PATCH - Add rating to booking
    @PatchMapping("/{id}/rating")
    public ResponseEntity<?> addRating(@PathVariable String id,
                                      @RequestParam Integer score,
                                      @RequestParam(required = false) String review) {
        try {
            Booking booking = bookingService.addRating(id, score, review);
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error adding rating: " + e.getMessage());
        }
    }

    // PATCH - Update preparation status
    @PatchMapping("/{id}/preparation")
    public ResponseEntity<?> updatePreparationStatus(@PathVariable Long id) {
        try {
            Optional<Booking> bookingOpt = bookingService.findById(id);
            if (bookingOpt == null) {
                return ResponseEntity.notFound().build();
            }

            Booking booking = bookingOpt.get();
            booking.setStatus(BookingStatus.PREPARATION);
            booking.setPreparationTime(LocalDateTime.now());
            booking.setUpdatedAt(LocalDateTime.now());

            Booking updatedBooking = bookingService.save(booking);
            return ResponseEntity.ok(updatedBooking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating preparation status: " + e.getMessage());
        }
    }

    // GET - Booking statistics
    @GetMapping("/statistics")
    public ResponseEntity<?> getBookingStatistics(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String therapistId) {
        try {
            if (customerId != null) {
                long count = bookingService.getBookingCountByCustomer(customerId);
                return ResponseEntity.ok().body("{\"totalBookings\": " + count + "}");
            } else if (therapistId != null) {
                long count = bookingService.getBookingCountByTherapist(therapistId);
                return ResponseEntity.ok().body("{\"totalBookings\": " + count + "}");
            } else {
                long total = bookingService.findAll().size();
                long pending = bookingService.getBookingCountByStatus(BookingStatus.PENDING);
                long completed = bookingService.getBookingCountByStatus(BookingStatus.COMPLETED);
                
                String stats = String.format(
                    "{\"total\": %d, \"pending\": %d, \"completed\": %d}",
                    total, pending, completed
                );
                return ResponseEntity.ok().body(stats);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error retrieving statistics: " + e.getMessage());
        }
    }

    // DELETE - Delete booking
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Long id) {
        try {
            if (!bookingService.findById(id).isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            bookingService.deleteById(id);
            return ResponseEntity.ok().body("Booking deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting booking: " + e.getMessage());
        }
    }
}