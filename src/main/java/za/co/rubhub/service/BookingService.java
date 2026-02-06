package za.co.rubhub.service;

import za.co.rubhub.model.Booking;
import za.co.rubhub.model.BookingStatus;
import za.co.rubhub.model.User;
import za.co.rubhub.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TherapistService therapistService;

    // Basic CRUD operations
    public List<Booking> findAll() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    public Booking findByRequestId(String requestId) {
        return bookingRepository.findByRequestId(Long.parseLong(requestId));
    }

    public List<Booking> findByCustomerId(String customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }

    public List<Booking> findByTherapistId(String therapistId) {
        return bookingRepository.findByTherapistId(Long.parseLong(therapistId));
    }

    public List<Booking>  findByStatus(BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    public Booking save(Booking booking) {
        // Calculate totals before saving
        booking.calculateTotals();
        
        return bookingRepository.save(booking);
    }

    public void deleteById(Long id) {
        bookingRepository.deleteById(id);
    }

    // Business logic methods
    public Booking createBooking(Booking booking) {
        // Validate customer exists
        Optional<User> customer = userService.findByEmail(booking.getCustomerEmail());
        if (customer == null) {
            throw new RuntimeException("Customer not found with Name: " + booking.getCustomerName());
        }

        // Validate therapist exists and is available
        if (therapistService.findById(booking.getTherapist().getId()) == null)
        {
            throw new RuntimeException("Therapist is not available for booking");
        }

        // Check for scheduling conflicts
        if (hasSchedulingConflict(booking)) {
            throw new RuntimeException("Scheduling conflict: Therapist already has a booking at this time");
        }

        booking.setStatus(BookingStatus.PENDING);
        booking.setPaymentStatus("pending");
        
        return save(booking);
    }

    public Booking acceptBooking(String bookingId, String therapistId) {
        Booking bookingOpt = findByRequestId(bookingId);
        if (bookingOpt != null) {
            Booking booking = bookingOpt;
            // Verify therapist matches
            if (!booking.getTherapist().getId().equals(therapistId)) {
                throw new RuntimeException("Therapist ID does not match booking");
            }
            booking.setStatus(BookingStatus.ACCEPTED);
            booking.setUpdatedAt(LocalDateTime.now());
            
            return save(booking);
        }
        throw new RuntimeException("Booking not found with ID: " + bookingId);
    }

    public Booking rejectBooking(String bookingId, String therapistId, String reason) {
        Optional<Booking> bookingOpt = findById(Long.parseLong(bookingId));
        if (bookingOpt == null) {
            Booking booking = bookingOpt.get();
            
            // Verify therapist matches
            if (!booking.getTherapist().getId().equals(therapistId)) {
                throw new RuntimeException("Therapist ID does not match booking");
            }

            booking.setStatus(BookingStatus.CANCELLED);
            booking.setUpdatedAt(LocalDateTime.now());
            // if (booking.getNotes() == null) {
            //     booking.setNotes("Rejected by therapist: " + reason);
            // } else {
            //     booking.setNotes(booking.getNotes() + "\nRejected by therapist: " + reason);
            // }
            
            return save(booking);
        }
        throw new RuntimeException("Booking not found with ID: " + bookingId);
    }

    public Booking startBooking(String bookingId) {
        Optional<Booking> bookingOpt = findById(Long.parseLong(bookingId));
        if (bookingOpt == null) {
            Booking booking = bookingOpt.get();
            
            if (booking.getStatus() != BookingStatus.ACCEPTED && 
                booking.getStatus() != BookingStatus.PREPARATION) {
                throw new RuntimeException("Booking must be accepted or in preparation to start");
            }

            booking.setStatus(BookingStatus.IN_PROGRESS);
            booking.setActualStartTime(LocalDateTime.now());
            booking.setUpdatedAt(LocalDateTime.now());
            
            return save(booking);
        }
        throw new RuntimeException("Booking not found with ID: " + bookingId);
    }

    public Booking completeBooking(String bookingId) {
        Optional<Booking> bookingOpt = findById(Long.parseLong(bookingId));
        if (bookingOpt == null) {
            Booking booking = bookingOpt.get();
            
            if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
                throw new RuntimeException("Booking must be in progress to complete");
            }

            booking.setStatus(BookingStatus.COMPLETED);
            booking.setActualEndTime(LocalDateTime.now());
            booking.setUpdatedAt(LocalDateTime.now());
            
            // Update therapist completed sessions count
            therapistService.incrementCompletedSessions(booking.getTherapist().getId());
            
            return save(booking);
        }
        throw new RuntimeException("Booking not found with ID: " + bookingId);
    }

    public Booking cancelBooking(String bookingId, String reason) {
        Optional<Booking> bookingOpt = findById(Long.parseLong(bookingId));
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            
            // Only allow cancellation if not completed or in progress
            if (booking.getStatus() == BookingStatus.COMPLETED || 
                booking.getStatus() == BookingStatus.IN_PROGRESS) {
                throw new RuntimeException("Cannot cancel completed or in-progress booking");
            }

            booking.setStatus(BookingStatus.CANCELLED);
            booking.setUpdatedAt(LocalDateTime.now());
            // if (booking.getNotes() == null) {
            //     booking.setNotes("Cancelled: " + reason);
            // } else {
            //     booking.setNotes(booking.getNotes() + "\nCancelled: " + reason);
            // }
            
            return save(booking);
        }
        throw new RuntimeException("Booking not found with ID: " + bookingId);
    }

    public Booking updatePaymentStatus(Long bookingId, String paymentStatus, String transactionId) {
        Optional<Booking> bookingOpt = findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            
            booking.setPaymentStatus(paymentStatus);
            booking.setUpdatedAt(LocalDateTime.now());
            if (transactionId != null) {
                // if (booking.getNotes() == null) {
                //     booking.setNotes("Payment transaction: " + transactionId);
                // } else {
                //     booking.setNotes(booking.getNotes() + "\nPayment transaction: " + transactionId);
                // }
            }
            
            return save(booking);
        }
        throw new RuntimeException("Booking not found with ID: " + bookingId);
    }

    public Booking addRating(String bookingId, Integer score, String review) {
        Optional<Booking> bookingOpt = findById(Long.parseLong(bookingId));
        if (bookingOpt == null) {
            Booking booking = bookingOpt.get();
            
            if (booking.getStatus() != BookingStatus.COMPLETED) {
                throw new RuntimeException("Can only rate completed bookings");
            }

            if (score < 1 || score > 5) {
                throw new RuntimeException("Rating score must be between 1 and 5");
            }

            // rating.setScore(score);
            // rating.setReview(review);
            // rating.setCreatedAt(LocalDateTime.now());
            
            // booking.setRating(rating);
            booking.setUpdatedAt(LocalDateTime.now());
            
            // Update therapist rating
            // therapistService.updateRating(booking.getTherapist().getId(), score.doubleValue());
            
            return save(booking);
        }
        throw new RuntimeException("Booking not found with ID: " + bookingId);
    }

    // Search and filter methods
    public List<Booking> findActiveBookings() {
        return bookingRepository.findActiveBookings();
    }

    public List<Booking> findCustomerBookingsByStatus(String customerId, BookingStatus status) {
        return bookingRepository.findByCustomerIdAndStatus(customerId, status);
    }

    public List<Booking> findTherapistBookingsByStatus(String therapistId, BookingStatus status) {
        return bookingRepository.findByTherapistIdAndStatus(therapistId, status);
    }

    public List<Booking> findBookingsByDateRange(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByScheduledTimeBetween(start, end);
    }

    public List<Booking> findBookingsReadyForPayout() {
        return bookingRepository.findBookingsReadyForPayout();
    }

    // Utility methods
    private boolean hasSchedulingConflict(Booking booking) {
        LocalDateTime start = (LocalDateTime) booking.getScheduledTime();
        LocalDateTime end = start.plusMinutes(booking.getServiceType().getDuration());
        
        // List<Booking> conflicts = bookingRepository.findTherapistConflicts(
        //     booking.getTherapist().getId(), start, end);
        
        // return !conflicts.isEmpty();
        return false;
    }

    // Statistics methods
    public long getBookingCountByCustomer(String customerId) {
        return bookingRepository.countByCustomerId(customerId);
    }

    public long getBookingCountByTherapist(String therapistId) {
        return bookingRepository.countByTherapistId(therapistId);
    }

    public long getBookingCountByStatus(BookingStatus status) {
        return bookingRepository.countByStatus(status);
    }
}