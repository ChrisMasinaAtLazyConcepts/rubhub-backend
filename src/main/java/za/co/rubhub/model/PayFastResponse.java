package za.co.rubhub.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import za.co.rubhub.model.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayFastResponse {
    
    // Common response fields
    private String status;
    private String message;
    private String code;
    private String data;
    
    // Payment response fields
    @JsonProperty("m_payment_id")
    private String mPaymentId;
    
    @JsonProperty("pf_payment_id")
    private String pfPaymentId;
    
    @JsonProperty("payment_status")
    private String paymentStatus;
    
    @JsonProperty("item_name")
    private String itemName;
    
    @JsonProperty("item_description")
    private String itemDescription;
    
    @JsonProperty("amount_gross")
    private BigDecimal amountGross;
    
    @JsonProperty("amount_fee")
    private BigDecimal amountFee;
    
    @JsonProperty("amount_net")
    private BigDecimal amountNet;
    
    @JsonProperty("name_first")
    private String nameFirst;
    
    @JsonProperty("name_last")
    private String nameLast;
    
    @JsonProperty("email_address")
    private String emailAddress;
    
    @JsonProperty("cell_number")
    private String cellNumber;
    
    // Custom fields
    @JsonProperty("custom_str1")
    private String customStr1;
    
    @JsonProperty("custom_str2")
    private String customStr2;
    
    @JsonProperty("custom_str3")
    private String customStr3;
    
    @JsonProperty("custom_str4")
    private String customStr4;
    
    @JsonProperty("custom_str5")
    private String customStr5;
    
    @JsonProperty("custom_int1")
    private Integer customInt1;
    
    @JsonProperty("custom_int2")
    private Integer customInt2;
    
    @JsonProperty("custom_int3")
    private Integer customInt3;
    
    @JsonProperty("custom_int4")
    private Integer customInt4;
    
    @JsonProperty("custom_int5")
    private Integer customInt5;
    
    // Payout response fields
    @JsonProperty("payout_id")
    private String payoutId;
    
    @JsonProperty("reference")
    private String reference;
    
    @JsonProperty("amount")
    private BigDecimal amount;
    
    @JsonProperty("fee")
    private BigDecimal fee;
    
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    @JsonProperty("scheduled_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String scheduledDate;
    
    @JsonProperty("processed_date")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime processedDate;
    
    @JsonProperty("status_code")
    private String statusCode;
    
    @JsonProperty("beneficiary_id")
    private String beneficiaryId;
    
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
    
    // Subscription response fields
    @JsonProperty("token")
    private String token;
    
    @JsonProperty("billing_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String billingDate;
    
    @JsonProperty("recurring_amount")
    private BigDecimal recurringAmount;
    
    @JsonProperty("cycles")
    private Integer cycles;
    
    @JsonProperty("frequency")
    private Integer frequency;
    
    @JsonProperty("subscription_type")
    private String subscriptionType;
    
    // Merchant fields
    @JsonProperty("merchant_id")
    private String merchantId;
    
    @JsonProperty("merchant_key")
    private String merchantKey;
    
    // Payment method
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    // Bank details
    @JsonProperty("bank_name")
    private String bankName;
    
    @JsonProperty("bank_account_type")
    private String bankAccountType;
    
    // Validation/verification fields
    @JsonProperty("signature")
    private String signature;
    
    @JsonProperty("signature_valid")
    private Boolean signatureValid;
    
    // Error details
    @JsonProperty("error_code")
    private String errorCode;
    
    @JsonProperty("error_message")
    private String errorMessage;
    
    // Response type indicators
    @JsonProperty("response_type")
    private String responseType; // "payment", "payout", "subscription", "itn", etc.
    
    // ITN (Instant Transaction Notification) specific
    @JsonProperty("itn_received")
    private Boolean itnReceived;
    
    @JsonProperty("itn_verified")
    private Boolean itnVerified;
    
    // Additional data (for complex responses)
    private Map<String, Object> additionalData;
    
    // Helper methods
    
    /**
     * Check if the response indicates success
     */
    public boolean isSuccess() {
        return "success".equalsIgnoreCase(status) || 
               "COMPLETE".equalsIgnoreCase(paymentStatus) ||
               "processed".equalsIgnoreCase(status) ||
               "completed".equalsIgnoreCase(status);
    }
    
    /**
     * Check if this is a payout response
     */
    public boolean isPayoutResponse() {
        return payoutId != null || beneficiaryId != null;
    }
    
    /**
     * Check if this is a payment response
     */
    public boolean isPaymentResponse() {
        return pfPaymentId != null || paymentStatus != null;
    }
    
    /**
     * Check if this is a subscription response
     */
    public boolean isSubscriptionResponse() {
        return token != null || subscriptionType != null;
    }
    
    /**
     * Check if this is an ITN response
     */
    public boolean isItnResponse() {
        return Boolean.TRUE.equals(itnReceived);
    }
    
    /**
     * Get formatted amount for display
     */
    public String getFormattedAmount() {
        if (amount != null) {
            return String.format("R %.2f", amount);
        } else if (amountGross != null) {
            return String.format("R %.2f", amountGross);
        }
        return "R 0.00";
    }
    
    /**
     * Get the booking ID from custom fields
     */
    public String getBookingId() {
        return customStr1 != null ? customStr1 : 
               (customStr2 != null ? customStr2 : null);
    }
    
    /**
     * Get the user ID from custom fields
     */
    public String getUserId() {
        return customStr3 != null ? customStr3 : 
               (customStr4 != null ? customStr4 : null);
    }
}


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class PayFastPaymentRequest {
    @JsonProperty("merchant_id")
    private String merchantId;
    
    @JsonProperty("merchant_key")
    private String merchantKey;
    
    @JsonProperty("return_url")
    private String returnUrl;
    
    @JsonProperty("cancel_url")
    private String cancelUrl;
    
    @JsonProperty("notify_url")
    private String notifyUrl;
    
    @JsonProperty("name_first")
    private String nameFirst;
    
    @JsonProperty("name_last")
    private String nameLast;
    
    @JsonProperty("email_address")
    private String emailAddress;
    
    @JsonProperty("cell_number")
    private String cellNumber;
    
    @JsonProperty("m_payment_id")
    private String mPaymentId;
    
    @JsonProperty("amount")
    private BigDecimal amount;
    
    @JsonProperty("item_name")
    private String itemName;
    
    @JsonProperty("item_description")
    private String itemDescription;
    
    @JsonProperty("email_confirmation")
    private Integer emailConfirmation = 1;
    
    @JsonProperty("confirmation_address")
    private String confirmationAddress;
    
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    @JsonProperty("subscription_type")
    private Integer subscriptionType;
    
    @JsonProperty("recurring_amount")
    private BigDecimal recurringAmount;
    
    @JsonProperty("frequency")
    private Integer frequency;
    
    @JsonProperty("cycles")
    private Integer cycles;
    
    @JsonProperty("billing_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String billingDate;
    
    @JsonProperty("custom_str1")
    private String customStr1;
    
    @JsonProperty("custom_str2")
    private String customStr2;
    
    @JsonProperty("custom_str3")
    private String customStr3;
    
    @JsonProperty("custom_str4")
    private String customStr4;
    
    @JsonProperty("custom_str5")
    private String customStr5;
    
    @JsonProperty("custom_int1")
    private Integer customInt1;
    
    @JsonProperty("custom_int2")
    private Integer customInt2;
    
    @JsonProperty("custom_int3")
    private Integer customInt3;
    
    @JsonProperty("custom_int4")
    private Integer customInt4;
    
    @JsonProperty("custom_int5")
    private Integer customInt5;
    
    @JsonProperty("signature")
    private String signature;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class PayFastITNRequest {
    @JsonProperty("m_payment_id")
    private String mPaymentId;
    
    @JsonProperty("pf_payment_id")
    private String pfPaymentId;
    
    @JsonProperty("payment_status")
    private String paymentStatus;
    
    @JsonProperty("item_name")
    private String itemName;
    
    @JsonProperty("item_description")
    private String itemDescription;
    
    @JsonProperty("amount_gross")
    private BigDecimal amountGross;
    
    @JsonProperty("amount_fee")
    private BigDecimal amountFee;
    
    @JsonProperty("amount_net")
    private BigDecimal amountNet;
    
    @JsonProperty("name_first")
    private String nameFirst;
    
    @JsonProperty("name_last")
    private String nameLast;
    
    @JsonProperty("email_address")
    private String emailAddress;
    
    @JsonProperty("cell_number")
    private String cellNumber;
    
    @JsonProperty("custom_str1")
    private String customStr1;
    
    @JsonProperty("custom_str2")
    private String customStr2;
    
    @JsonProperty("custom_str3")
    private String customStr3;
    
    @JsonProperty("custom_str4")
    private String customStr4;
    
    @JsonProperty("custom_str5")
    private String customStr5;
    
    @JsonProperty("custom_int1")
    private Integer customInt1;
    
    @JsonProperty("custom_int2")
    private Integer customInt2;
    
    @JsonProperty("custom_int3")
    private Integer customInt3;
    
    @JsonProperty("custom_int4")
    private Integer customInt4;
    
    @JsonProperty("custom_int5")
    private Integer customInt5;
    
    @JsonProperty("signature")
    private String signature;
    
    // Helper method to check if payment is complete
    public boolean isPaymentComplete() {
        return "COMPLETE".equalsIgnoreCase(paymentStatus);
    }
    
    // Helper method to check if payment failed
    public boolean isPaymentFailed() {
        return "FAILED".equalsIgnoreCase(paymentStatus) || 
               "CANCELLED".equalsIgnoreCase(paymentStatus) ||
               "ABORTED".equalsIgnoreCase(paymentStatus);
    }
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class PayFastRefundRequest {
    @JsonProperty("merchant_id")
    private String merchantId;
    
    @JsonProperty("merchant_key")
    private String merchantKey;
    
    @JsonProperty("pf_payment_id")
    private String pfPaymentId;
    
    @JsonProperty("amount")
    private BigDecimal amount;
    
    @JsonProperty("reason")
    private String reason;
    
    @JsonProperty("signature")
    private String signature;
    
    @JsonProperty("test_mode")
    private Boolean testMode = false;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class PayFastSubscriptionRequest {
    @JsonProperty("merchant_id")
    private String merchantId;
    
    @JsonProperty("merchant_key")
    private String merchantKey;
    
    @JsonProperty("token")
    private String token;
    
    @JsonProperty("amount")
    private BigDecimal amount;
    
    @JsonProperty("item_name")
    private String itemName;
    
    @JsonProperty("item_description")
    private String itemDescription;
    
    @JsonProperty("start_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String startDate;
    
    @JsonProperty("cycles")
    private Integer cycles;
    
    @JsonProperty("frequency")
    private Integer frequency;
    
    @JsonProperty("signature")
    private String signature;
}

// Enum for PayFast status codes
enum PayFastStatus {
    COMPLETE("Payment completed successfully"),
    FAILED("Payment failed"),
    CANCELLED("Payment was cancelled"),
    PENDING("Payment is pending"),
    PROCESSING("Payment is being processed"),
    ABORTED("Payment was aborted"),
    EXPIRED("Payment has expired");
    
    private final String description;
    
    PayFastStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static PayFastStatus fromString(String status) {
        if (status == null) return null;
        try {
            return PayFastStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}