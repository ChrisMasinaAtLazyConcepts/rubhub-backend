// SecurityController.java
package za.co.rubhub.controller;

import za.co.rubhub.model.*;
import za.co.rubhub.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SecurityController {
    
    private final SecurityService securityService;
    
    // Security Alerts Endpoints
    @GetMapping("/alerts")
    public ResponseEntity<List<SecurityAlert>> getActiveAlerts() {
        try {
            List<SecurityAlert> alerts = securityService.getActiveAlerts();
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/alerts/flashing")
    public ResponseEntity<List<SecurityAlert>> getFlashingAlerts() {
        try {
            List<SecurityAlert> alerts = securityService.getFlashingAlerts();
            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/alerts")
    public ResponseEntity<SecurityAlert> createAlert(@RequestBody SecurityAlert alert) {
        try {
            SecurityAlert createdAlert = securityService.createSecurityAlert(alert);
            return ResponseEntity.ok(createdAlert);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PutMapping("/alerts/{id}/status")
    public ResponseEntity<SecurityAlert> updateAlertStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            SecurityAlert alert = securityService.updateAlertStatus(
                id, SecurityAlert.AlertStatus.valueOf(status));
            return ResponseEntity.ok(alert);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/alerts/{id}/resolve")
    public ResponseEntity<SecurityAlert> resolveAlert(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String notes = request.get("notes");
            String resolvedBy = request.get("resolvedBy");
            SecurityAlert alert = securityService.resolveAlert(id, notes, resolvedBy);
            return ResponseEntity.ok(alert);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/alerts/{id}/actions")
    public ResponseEntity<SecurityAlert> addAlertAction(
            @PathVariable Long id,
            @RequestBody SecurityAlert.ActionTaken action) {
        try {
            SecurityAlert alert = securityService.addActionToAlert(id, action);
            return ResponseEntity.ok(alert);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Emergency Actions Endpoints
    @PostMapping("/alerts/{id}/notify-saps")
    public ResponseEntity<Void> notifySAPS(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String sapsReference = request.get("sapsReference");
            securityService.notifySAPS(id, sapsReference);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/alerts/{id}/notify-security")
    public ResponseEntity<Void> notifySecurityCompany(@PathVariable Long id) {
        try {
            securityService.notifySecurityCompany(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/alerts/{id}/send-saps-email")
    public ResponseEntity<Void> sendSAPSEmail(@PathVariable Long id) {
        try {
            securityService.sendSAPSEmail(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    

    @GetMapping("/dashboard/metrics")
    public ResponseEntity<SecurityMetrics> getSecurityMetrics(
            @RequestParam(defaultValue = "30d") String timeRange) {
        try {
            SecurityMetrics metrics = securityService.getSecurityMetrics(timeRange);
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/dashboard/statistics")
    public ResponseEntity<List<Map<String, Object>>> getAlertStatistics() {
        try {
            List<Map<String, Object>> statistics = securityService.getAlertStatistics();
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
        
    @PostMapping("/reports/generate")
    public ResponseEntity<Void> generateDailyReport() {
        try {
            securityService.generateDailySecurityReport();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}