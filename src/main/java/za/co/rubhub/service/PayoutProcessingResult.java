package za.co.rubhub.service;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PayoutProcessingResult {
    private LocalDateTime processingDate;
    private int totalBookings;
    private int successfulPayouts;
    private int failedPayouts;
    private BigDecimal totalProcessedAmount = BigDecimal.ZERO;
    private BigDecimal totalTherapistPayouts = BigDecimal.ZERO;
    private BigDecimal totalRubhubFees = BigDecimal.ZERO;
    private List<TherapistSummary> therapistSummaries = new ArrayList<>();
    
    public void incrementSuccessfulPayouts() {
        this.successfulPayouts++;
    }
    
    public void incrementFailedPayouts() {
        this.failedPayouts++;
    }
    
    public void addProcessedAmount(BigDecimal amount) {
        this.totalProcessedAmount = this.totalProcessedAmount.add(amount);
    }
    
    public void addTherapistSummary(String therapistId, int bookingsCount, 
                                  BigDecimal therapistAmount, BigDecimal rubhubFees) {
        TherapistSummary summary = new TherapistSummary();
        summary.setTherapistId(therapistId);
        summary.setBookingsCount(bookingsCount);
        summary.setTherapistAmount(therapistAmount);
        summary.setRubhubFees(rubhubFees);
        this.therapistSummaries.add(summary);
        
        this.totalTherapistPayouts = this.totalTherapistPayouts.add(therapistAmount);
        this.totalRubhubFees = this.totalRubhubFees.add(rubhubFees);
    }
    
    @Data
    public static class TherapistSummary {
        private String therapistId;
        private int bookingsCount;
        private BigDecimal therapistAmount;
        private BigDecimal rubhubFees;
    }
}