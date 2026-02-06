// SecurityServiceImpl.java
package za.co.rubhub.service;

import za.co.rubhub.model.*;
import za.co.rubhub.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {
    
    private final SecurityAlertRepository securityAlertRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    
    // Security Alerts Management
    public SecurityAlert createSecurityAlert(SecurityAlert alert) {
        log.info("Creating security alert of type: {}", alert.getType());
        
        alert.setTimestamp(LocalDateTime.now());
        alert.setIsFlashing(true); // New alerts start as flashing
        
        // Set priority based on alert type
        switch (alert.getType()) {
            case PANIC_BUTTON:
                alert.setPriority(5);
                break;
            case LOCATION_ANOMALY:
                alert.setPriority(4);
                break;
            case DURATION_OVERRUN:
                alert.setPriority(3);
                break;
            default:
                alert.setPriority(2);
        }
        
        SecurityAlert savedAlert = securityAlertRepository.save(alert);
        
        // Automatically notify emergency contacts for high-priority alerts
        if (alert.getPriority() >= 4) {
            notifyEmergencyContacts(savedAlert.getId());
        }
        
        log.info("Security alert created with ID: {}", savedAlert.getId());
        return savedAlert;
    }
    
    public SecurityAlert updateAlertStatus(Long alertId, SecurityAlert.AlertStatus status) {
        log.info("Updating alert {} status to: {}", alertId, status);
        
        return securityAlertRepository.findById(alertId)
                .map(alert -> {
                    alert.setStatus(status);
                    
                    if (status == SecurityAlert.AlertStatus.RESOLVED) {
                        alert.setResolvedAt(LocalDateTime.now());
                        alert.setIsFlashing(false);
                    } else if (status == SecurityAlert.AlertStatus.ACTIVE) {
                        alert.setIsFlashing(true);
                    }
                    
                    return securityAlertRepository.save(alert);
                })
                .orElseThrow(() -> new RuntimeException("Security alert not found: " + alertId));
    }
    
    public SecurityAlert addActionToAlert(Long alertId, SecurityAlert.ActionTaken action) {
        log.info("Adding action {} to alert: {}", action.getAction(), alertId);
        
        return securityAlertRepository.findById(alertId)
                .map(alert -> {
                    action.setTimestamp(LocalDateTime.now());
                    
                    if (alert.getActionsTaken() == null) {
                        alert.setActionsTaken(new ArrayList<>());
                    }
                    alert.getActionsTaken().add(action);
                    
                    return securityAlertRepository.save(alert);
                })
                .orElseThrow(() -> new RuntimeException("Security alert not found: " + alertId));
    }
    
    public List<SecurityAlert> getActiveAlerts() {
        log.info("Fetching active security alerts");
        return securityAlertRepository.findByStatus(SecurityAlert.AlertStatus.ACTIVE);
    }
    
    public List<SecurityAlert> getFlashingAlerts() {
        log.info("Fetching flashing security alerts");
        return securityAlertRepository.findByIsFlashingTrue();
    }
    
    public SecurityAlert resolveAlert(Long alertId, String resolutionNotes, String resolvedBy) {
        log.info("Resolving alert: {}", alertId);
        
        return securityAlertRepository.findById(alertId)
                .map(alert -> {
                    alert.setStatus(SecurityAlert.AlertStatus.RESOLVED);
                    alert.setResolvedAt(LocalDateTime.now());
                    alert.setResolutionNotes(resolutionNotes);
                    alert.setResolvedBy(resolvedBy);
                    alert.setIsFlashing(false);
                    
                    
                    return securityAlertRepository.save(alert);
                })
                .orElseThrow(() -> new RuntimeException("Security alert not found: " + alertId));
    }
    
    public void saveRecording(Long alertId, String recordingUrl, Long duration) {
        log.info("Saving recording for alert: {}, duration: {}s", alertId, duration);
        
        securityAlertRepository.findById(alertId)
                .ifPresent(alert -> {
                    alert.setRecordingUrl(recordingUrl);
                    alert.setRecordingDuration(duration);
                    alert.setRecordingEndedAt(LocalDateTime.now());
                    securityAlertRepository.save(alert);
                });
    }
    
    
    // Emergency Actions
    public void notifySAPS(Long alertId, String sapsReference) {
        log.info("Notifying SAPS for alert: {}", alertId);
        
        securityAlertRepository.findById(alertId)
                .ifPresent(alert -> {
                    SecurityAlert.NotificationStatus notifications = alert.getNotifications();
                    notifications.setSaps(true);
                    notifications.setSapsNotifiedAt(LocalDateTime.now());
                    notifications.setSapsReference(sapsReference);
                    alert.setNotifications(notifications);
                    
                    securityAlertRepository.save(alert);
                    
                    // Record the action
                    SecurityAlert.ActionTaken action = new SecurityAlert.ActionTaken();
                    // action.setAction(PanicAction.CALL_SAPS);
                    action.setPerformedBy("system");
                    action.setNotes("SAPS notified with reference: " + sapsReference);
                    // addActionToAlert(alertId, action);
                    
                    log.info("SAPS notified successfully for alert: {}", alertId);
                });
    }
    
    public void notifySecurityCompany(Long alertId) {
        log.info("Notifying security company for alert: {}", alertId);
        
        securityAlertRepository.findById(alertId)
                .ifPresent(alert -> {
                    SecurityAlert.NotificationStatus notifications = alert.getNotifications();
                    notifications.setSecurityCompany(true);
                    alert.setNotifications(notifications);
                    
                    securityAlertRepository.save(alert);
                    
                    // Record the action
                    SecurityAlert.ActionTaken action = new SecurityAlert.ActionTaken();
                    // action.setAction(PanicAction.NOTIFY_SECURITY_COMPANY);
                    action.setPerformedBy("system");
                    action.setNotes("Security company " + alert.getSecurityCompany().getName() + " notified");
                    // addActionToAlert(alertId, action);
                    
                    log.info("Security company notified successfully for alert: {}", alertId);
                });
    }
    
    public void notifyEmergencyContacts(Long alertId) {
        log.info("Notifying emergency contacts for alert: {}", alertId);
        
        securityAlertRepository.findById(alertId)
                .ifPresent(alert -> {
                    SecurityAlert.NotificationStatus notifications = alert.getNotifications();
                    notifications.setEmergencyContacts(true);
                    alert.setNotifications(notifications);
                    
                    securityAlertRepository.save(alert);
                    
                    log.info("Emergency contacts notified for alert: {}", alertId);
                });
    }
    
    public void sendSAPSEmail(Long alertId) {
        log.info("Sending SAPS email for alert: {}", alertId);
        
        securityAlertRepository.findById(alertId)
                .ifPresent(alert -> {
                    // In a real implementation, this would send an email to SAPS
                    // with all the alert details
                    
                    // Record the action
                    SecurityAlert.ActionTaken action = new SecurityAlert.ActionTaken();
                    // action.setAction(PanicAction.SEND_SAPS_EMAIL);
                    action.setPerformedBy("system");
                    action.setNotes("SAPS email sent with detailed incident report");
                    // addActionToAlert(alertId, action);
                    
                    log.info("SAPS email sent for alert: {}", alertId);
                });
    }
    
    // Dashboard Data
    public SecurityMetrics getSecurityMetrics(String timeRange) {
        log.info("Generating security metrics for time range: {}", timeRange);
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = calculateStartDate(timeRange, now);
        
        // Try to get cached metrics first
        // return securityAlertRepository.findLatestByTimeRange(timeRange, now.minusHours(1));
                // .orElseGet(() -> generateSecurityMetrics(timeRange, startDate, now));
                return null;
    }
    
    public List<Map<String, Object>> getAlertStatistics() {
        log.info("Fetching alert statistics");
        
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime now = LocalDateTime.now();
        
        List<Map<String, Object>> statistics = new ArrayList<>();
        
        // Count alerts by type for today
        for (SecurityAlert.AlertType type : SecurityAlert.AlertType.values()) {
            long count = securityAlertRepository.countByAlertTypeAndTimestampBetween(type, todayStart, now);
            
            Map<String, Object> stat = new HashMap<>();
            stat.put("type", type.toString());
            stat.put("count", count);
            statistics.add(stat);
        }
        
        return statistics;
    }
    
    public void generateDailySecurityReport() {
        log.info("Generating daily security report");
        
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        
        generateSecurityMetrics("daily", yesterday, today);
    }
    
    // Private helper methods
    private SecurityMetrics generateSecurityMetrics(String timeRange, LocalDateTime start, LocalDateTime end) {
        SecurityMetrics metrics = new SecurityMetrics();
        metrics.setDate(start);
        metrics.setTimeRange(timeRange);
        metrics.setGeneratedAt(LocalDateTime.now());
        
        // Calculate various metrics
        List<SecurityAlert> alerts = securityAlertRepository.findByTimestampBetween(start, end);
        
        // Basic counts
        metrics.setActivePanicAlerts((int) alerts.stream()
                .filter(a -> a.getStatus() == SecurityAlert.AlertStatus.ACTIVE)
                .count());
        
        metrics.setSapsCallsToday((int) alerts.stream()
                .filter(a -> a.getNotifications() != null && a.getNotifications().getSaps())
                .count());
        
        metrics.setResolvedToday((int) alerts.stream()
                .filter(a -> a.getStatus() == SecurityAlert.AlertStatus.RESOLVED)
                .count());
        
        metrics.setTotalAlerts(alerts.size());
        
        // Calculate response times
        calculateResponseTimes(metrics, alerts);
        
        // Calculate geographic distribution
        calculateGeographicDistribution(metrics, alerts);
        
        // Calculate success metrics
        // calculateSuccessMetrics(metrics, alerts);
        
        // Stream metrics
        // calculateStreamMetrics(metrics, streams);
        
        //return securityAlertRepository.save(metrics);
        return null;
    }
    
    private void calculateResponseTimes(SecurityMetrics metrics, List<SecurityAlert> alerts) {
        // Implementation for calculating average response times
        // This would require tracking when alerts were acknowledged vs when they were created
    }
    
    private void calculateGeographicDistribution(SecurityMetrics metrics, List<SecurityAlert> alerts) {
        // Implementation for calculating alerts by area/city
    }
    
    private void calculateSuccessMetrics(SecurityMetrics metrics, List<SecurityAlert> alerts) {
        // Implementation for calculating resolution rates, false alarm rates, etc.
    }
    
    private LocalDateTime calculateStartDate(String timeRange, LocalDateTime endDate) {
        switch (timeRange) {
            case "7d":
                return endDate.minusDays(7);
            case "30d":
                return endDate.minusDays(30);
            case "90d":
                return endDate.minusDays(90);
            case "1y":
                return endDate.minusYears(1);
            default:
                return endDate.minusDays(30);
        }
    }

}
   