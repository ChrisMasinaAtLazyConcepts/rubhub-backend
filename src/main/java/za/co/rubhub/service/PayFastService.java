package za.co.rubhub.service;

import za.co.rubhub.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
@Slf4j
public class PayFastService {
    
    @Value("${payfast.merchant.id}")
    private String merchantId;
    
    @Value("${payfast.merchant.key}")
    private String merchantKey;
    
    @Value("${payfast.passphrase}")
    private String passphrase;
    
    @Value("${payfast.base.url}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;
    
    public PayFastService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }
    
    public PayFastResponse processPayout(Therapist therapist, BigDecimal amount, String reference) {
        try {
            // Prepare PayFast payout request
            PayFastPayoutRequest request = new PayFastPayoutRequest();
            request.setMerchantId(merchantId);
            request.setMerchantKey(merchantKey);
            request.setAmount(amount);
            request.setBeneficiaryId(therapist.getPayfastBeneficiaryId());
            request.setReference(reference);
            
            // Generate signature
            String signature = generateSignature(request);
            request.setSignature(signature);
            
            // Make API call to PayFast
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("merchant-id", merchantId);
            headers.set("version", "v1");
            
            HttpEntity<PayFastPayoutRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<PayFastResponse> response = restTemplate.postForEntity(
                baseUrl + "/payouts/submit", 
                entity, 
                PayFastResponse.class
            );
            
            log.info("PayFast payout response for therapist {}: {}", 
                    therapist.getId(), response.getBody());
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Error processing PayFast payout for therapist {}: {}", 
                     therapist.getId(), e.getMessage(), e);
            throw new RuntimeException("PayFast payout failed", e);
        }
    }
    
    private String generateSignature(PayFastPayoutRequest request) {
        try {
            Map<String, String> parameters = new LinkedHashMap<>();
            parameters.put("merchant_id", request.getMerchantId());
            parameters.put("merchant_key", request.getMerchantKey());
            parameters.put("amount", String.format("%.2f", request.getAmount()));
            parameters.put("beneficiary_id", request.getBeneficiaryId());
            parameters.put("reference", request.getReference());
            
            String payload = parameters.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));
            
            if (passphrase != null && !passphrase.isEmpty()) {
                payload += "&passphrase=" + passphrase;
            }
            
            return DigestUtils.md5Hex(payload).toLowerCase();
            
        } catch (Exception e) {
            log.error("Error generating PayFast signature: {}", e.getMessage(), e);
            throw new RuntimeException("Signature generation failed", e);
        }
    }
}