package za.co.rubhub.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import za.co.rubhub.model.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
 public class PayFastPayoutRequest {
    
    @JsonProperty("merchant_id")
    private String merchantId;
    
    @JsonProperty("merchant_key")
    private String merchantKey;
    
    @JsonProperty("amount")
    private BigDecimal amount;
    
    @JsonProperty("beneficiary_id")
    private String beneficiaryId;
    
    @JsonProperty("reference")
    private String reference;
    
    @JsonProperty("scheduled_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String scheduledDate;
    
    @JsonProperty("beneficiary_name")
    private String beneficiaryName;
    
    @JsonProperty("beneficiary_bank")
    private String beneficiaryBank;
    
    @JsonProperty("beneficiary_branch_code")
    private String beneficiaryBranchCode;
    
    @JsonProperty("beneficiary_account_number")
    private String beneficiaryAccountNumber;
    
    @JsonProperty("beneficiary_account_type")
    private String beneficiaryAccountType;
    
    @JsonProperty("test_mode")
    @Builder.Default
    private Boolean testMode = false;
    
    @JsonProperty("signature")
    private String signature;
    
    @JsonProperty("notify_url")
    private String notifyUrl;
    
    @JsonProperty("notify_email")
    private String notifyEmail;
    
    @JsonProperty("notify_sms")
    private String notifySms;
    
    // Helper methods
    public boolean isValid() {
        return merchantId != null && !merchantId.isEmpty() &&
               merchantKey != null && !merchantKey.isEmpty() &&
               amount != null && amount.compareTo(BigDecimal.ZERO) > 0 &&
               beneficiaryId != null && !beneficiaryId.isEmpty() &&
               reference != null && !reference.isEmpty();
    }
    
    public String getFormattedAmount() {
        return amount != null ? String.format("R %.2f", amount) : "R 0.00";
    }
}