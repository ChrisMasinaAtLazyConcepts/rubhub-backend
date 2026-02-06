package za.co.rubhub.service;

import za.co.rubhub.model.*;
import za.co.rubhub.model.Payment.PaymentStatus;
import za.co.rubhub.model.TherapistPayout.PayoutStatus;
import za.co.rubhub.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import javax.validation.Valid;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final BookingRepository bookingRepository;
    private final TherapistRepository therapistRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final EmailService emailService;
    private static final BigDecimal RUBHUB_FEE_PERCENTAGE = new BigDecimal("0.12");
    private static final String SUSPENSE_ACCOUNT_TYPE = "SUSPENSE";
    private static final String THERAPIST_ACCOUNT_TYPE = "THERAPIST";
    private static final String RUBHUB_ACCOUNT_TYPE = "RUBHUB";
 
    @Transactional
    public PayoutProcessingResult processTherapistPayouts() {
        log.info("Starting therapist payout processing...");
        
        LocalDateTime startOfWeek = LocalDateTime.now().minusWeeks(1).with(java.time.DayOfWeek.MONDAY);
        LocalDateTime endOfWeek = startOfWeek.with(java.time.DayOfWeek.SUNDAY).plusDays(1).minusSeconds(1);
        
        // Get completed bookings from last week that haven't been paid out
        List<Booking> completedBookings = bookingRepository
                .findCompletedBookingsForPayout(startOfWeek, endOfWeek);
        
        log.info("Found {} completed bookings for payout", completedBookings.size());
        
        PayoutProcessingResult result = new PayoutProcessingResult();
        result.setTotalBookings(completedBookings.size());
        result.setProcessingDate(LocalDateTime.now());
        
        // Group bookings by therapist
        Map<String, List<Booking>> bookingsByTherapist = new HashMap<>();
        for (Booking booking : completedBookings) {
            // bookingsByTherapist
                    // .computeIfAbsent(booking.getTherapist().getId(), k -> new ArrayList<>())
                    // .add(booking);
        }
        
        // Process payouts for each therapist
        for (Map.Entry<String, List<Booking>> entry : bookingsByTherapist.entrySet()) {
            String therapistId = entry.getKey();
            List<Booking> therapistBookings = entry.getValue();
            
            try {
                processTherapistPayout(therapistId, therapistBookings, result);
            } catch (Exception e) {
                log.error("Failed to process payout for therapist: {}", therapistId, e);
                result.incrementFailedPayouts();
            }
        }
        
        log.info("Payout processing completed: {}", result);
        return result;
    }
    
    private void processTherapistPayout(String therapistId, List<Booking> bookings, 
                                      PayoutProcessingResult result) {
        // Get therapist account
        Optional<Account> therapistAccountOpt = accountRepository
                .findByUserIdAndAccountType(therapistId, THERAPIST_ACCOUNT_TYPE);
        
        if (therapistAccountOpt== null) {
            log.warn("Therapist account not found for therapist: {}", therapistId);
            result.incrementFailedPayouts();
            return;
        }
        
        Account therapistAccount = therapistAccountOpt.get();
        Account suspenseAccount = getSuspenseAccount();
        Account rubhubAccount = getRubhubAccount();
        
        BigDecimal totalTherapistAmount = BigDecimal.ZERO;
        BigDecimal totalRubhubFees = BigDecimal.ZERO;
        
        // Process each booking
        for (Booking booking : bookings) {
            try {
                TherapistPayout payout = new TherapistPayout(
                );
                
                // Create payout record
                payout.setPayoutDate(LocalDateTime.now());
                // payout = payoutRepository.save(payout);
                
                // // Transfer from suspense to therapist account
                // boolean transferSuccess = transferFunds(
                //         suspenseAccount.getId(),
                //         therapistAccount.getId(),
                //         payout.getTherapistAmount(),
                //         "Therapist Payout - Booking: " + booking.getId(),
                //         payout.getId()
                // );
                
                // if (transferSuccess) {
                    // Transfer RubHub fee to RubHub account
                    // transferFunds(
                    //         suspenseAccount.getId(),
                    //         rubhubAccount.getId(),
                    //         payout.getRubhubFee(),
                    //         "RubHub Fee - Booking: " + booking.getId(),
                    //         payout.getId()
                    // );
                    
                    // Mark booking as processed
                    booking.setPayoutProcessed(true);
                    bookingRepository.save(booking);
                    
                    // Update payout status
                    payout.setPayoutStatus(PayoutStatus.PROCESSED);
                    payout.setProcessedAt(LocalDateTime.now());
                    // payoutRepository.save(payout);
                    
                    totalTherapistAmount = totalTherapistAmount.add(payout.getTherapistAmount());
                    totalRubhubFees = totalRubhubFees.add(payout.getRubhubFee());
                    
                    result.incrementSuccessfulPayouts();
                    result.addProcessedAmount(payout.getBookingAmount());
                    
                    log.info("Successfully processed payout for booking: {}", booking.getId());
                // } else {
                    // payout.setPayoutStatus("FAILED");
                    // payout.setFailureReason("Fund transfer failed");
                    // payoutRepository.save(payout);
                    // result.incrementFailedPayouts();
                // }
                
            } catch (Exception e) {
                log.error("Failed to process booking payout: {}", booking.getId(), e);
                result.incrementFailedPayouts();
            }
        }
        
        // Update result with therapist summary
        result.addTherapistSummary(therapistId, bookings.size(), totalTherapistAmount, totalRubhubFees);
    }
    
    private boolean transferFunds(Long fromAccountId, Long toAccountId, 
                                 BigDecimal amount, String description, String reference) {
        try {
            // Get accounts
            Optional<Account> fromAccountOpt = accountRepository.findById(fromAccountId);
            Optional<Account> toAccountOpt = accountRepository.findById(toAccountId);
            
            // if (fromAccountOpt.empty() || toAccountOpt.empty()) {
            //     log.error("Account not found for transfer: from={}, to={}", fromAccountId, toAccountId);
            //     return false;
            // }
            
            Account fromAccount = fromAccountOpt.get();
            Account toAccount = toAccountOpt.get();
            
            // Check sufficient balance
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                log.error("Insufficient balance in account: {}", fromAccountId);
                return false;
            }
            
            // Update balances
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            toAccount.setBalance(toAccount.getBalance().add(amount));
            
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);
            
            // Record transaction
            Transaction transaction = new Transaction();
            // transaction.setFromAccountId(fromAccountId);
            // transaction.setToAccountId(toAccountId);
            // transaction.setAmount(amount);
            // transaction.setTransactionType("TRANSFER");
            // transaction.setStatus("SUCCESS");
            transaction.setReference(reference);
            transaction.setTransactionDate(LocalDateTime.now());
            transaction.setDescription(description);
            
            transactionRepository.save(transaction);
            
            return true;
            
        } catch (Exception e) {
            log.error("Fund transfer failed: {}", e.getMessage(), e);
            return false;
        }
    }
    
    private Account getSuspenseAccount() {
        return accountRepository.findByAccountType(SUSPENSE_ACCOUNT_TYPE)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Suspense account not found"));
    }
    
    private Account getRubhubAccount() {
        return accountRepository.findByAccountType(RUBHUB_ACCOUNT_TYPE)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("RubHub account not found"));
    }

    public Optional<Payment> findById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    public List<Payment> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    public List<Payment> findByUserId(String userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUserId'");
    }

    public List<Payment> findByBookingId(String bookingId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByBookingId'");
    }

    public List<Payment> findByTherapistId(String therapistId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByTherapistId'");
    }

    public List<Payment> findByStatus(PaymentStatus status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByStatus'");
    }

    public Payment save(Payment payment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    public Payment processPaymentSuccess(String transactionId, String providerTransactionId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processPaymentSuccess'");
    }

    public Payment processPaymentFailure(String transactionId, String errorMessage) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processPaymentFailure'");
    }

    public List<Payment> findPaymentsForPayout() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findPaymentsForPayout'");
    }

    public Optional<Payment> findByTransactionId(String transactionId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByTransactionId'");
    }
    
    // public List<TherapistPayout> getPayoutsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
    //     return therapistRepository.findByPayoutDateBetween(startDate, endDate);
    // }
}