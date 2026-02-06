package za.co.rubhub.scheduler;

import za.co.rubhub.service.PaymentService;
import za.co.rubhub.service.PayoutProcessingResult;
import za.co.rubhub.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayoutScheduler {
    
    private final PaymentService paymentService;
    private final EmailService emailService;
    
    /**
     * Runs every Friday at 2:00 AM to process therapist payouts
     * for completed bookings from the previous week
     */
    @Scheduled(cron = "0 0 2 * * FRI") // Every Friday at 2:00 AM
    public void processWeeklyTherapistPayouts() {
        log.info("Starting scheduled therapist payout processing...");
        
        try {
            PayoutProcessingResult result = paymentService.processTherapistPayouts();
            
            // Send email report
            emailService.sendPayoutReport(result);
            
            log.info("Scheduled payout processing completed successfully");
            
        } catch (Exception e) {
            log.error("Scheduled payout processing failed: {}", e.getMessage(), e);
            
            // Send failure notification
            sendFailureNotification(e);
        }
    }
    
    /**
     * Additional safety check - runs every Monday at 9:00 AM
     * to catch any missed payouts from Friday
     */
    @Scheduled(cron = "0 0 9 * * MON") // Every Monday at 9:00 AM
    public void payoutSafetyCheck() {
        log.info("Running payout safety check...");
        
        try {
            PayoutProcessingResult result = paymentService.processTherapistPayouts();
            
            if (result.getTotalBookings() > 0) {
                log.warn("Found {} missed payouts during safety check", result.getTotalBookings());
                emailService.sendPayoutReport(result);
            } else {
                log.info("No missed payouts found during safety check");
            }
            
        } catch (Exception e) {
            log.error("Payout safety check failed: {}", e.getMessage(), e);
        }
    }
    
    private void sendFailureNotification(Exception e) {
        // Implement failure notification logic
        // This could be another email, Slack notification, etc.
        log.error("Payout processing failure notification would be sent here");
    }
}