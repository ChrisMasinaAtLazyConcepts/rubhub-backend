package za.co.rubhub.model;

import javax.persistence.*;
import lombok.*;
import za.co.rubhub.model.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "security_metrics")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecurityMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "date", nullable = false)
    private LocalDateTime date;
    
    @Column(name = "time_range", nullable = false, length = 20)
    private String timeRange; // "daily", "weekly", "monthly", "quarterly", "yearly"
    
    // KPI Metrics
    @Column(name = "active_panic_alerts")
    @Builder.Default
    private Integer activePanicAlerts = 0;
    
    @Column(name = "pending_selfie_checks")
    @Builder.Default
    private Integer pendingSelfieChecks = 0;
    
    @Column(name = "saps_calls_today")
    @Builder.Default
    private Integer sapsCallsToday = 0;
    
    @Column(name = "resolved_today")
    @Builder.Default
    private Integer resolvedToday = 0;
    
    @Column(name = "total_alerts")
    @Builder.Default
    private Integer totalAlerts = 0;
    
    @Column(name = "false_alarms")
    @Builder.Default
    private Integer falseAlarms = 0;
    
    // Response Time Metrics (in minutes)
    @Column(name = "average_response_time", precision = 5, scale = 2)
    @Builder.Default
    private Double averageResponseTime = 0.0;
    
    @Column(name = "average_resolution_time", precision = 5, scale = 2)
    @Builder.Default
    private Double averageResolutionTime = 0.0;
    
    @Column(name = "response_time_by_alert_type", columnDefinition = "jsonb")
    private String responseTimeByAlertType; // JSON: {"PANIC_BUTTON": 15.5, "LOCATION_ANOMALY": 8.2}
    
    // Geographic Distribution
    @Column(name = "alerts_by_area", columnDefinition = "jsonb")
    private String alertsByArea; // JSON: {"Sandton": 5, "Pretoria": 3}
    
    @Column(name = "alerts_by_city", columnDefinition = "jsonb")
    private String alertsByCity; // JSON: {"Johannesburg": 10, "Cape Town": 5}
    
    // Success Metrics
    @Column(name = "resolution_rate", precision = 5, scale = 2)
    @Builder.Default
    private Double resolutionRate = 0.0;
    
    @Column(name = "false_alarm_rate", precision = 5, scale = 2)
    @Builder.Default
    private Double falseAlarmRate = 0.0;
    
    @Column(name = "prevented_incidents")
    @Builder.Default
    private Integer preventedIncidents = 0;
    
    // Stream Metrics
    @Column(name = "total_streams_initiated")
    @Builder.Default
    private Integer totalStreamsInitiated = 0;
    
    @Column(name = "successful_streams")
    @Builder.Default
    private Integer successfulStreams = 0;
    
    @Column(name = "average_stream_duration", precision = 5, scale = 2)
    @Builder.Default
    private Double averageStreamDuration = 0.0;
    
    @Column(name = "recordings_saved")
    @Builder.Default
    private Integer recordingsSaved = 0;
    
    // Alert Type Distribution
    @Column(name = "alerts_by_type", columnDefinition = "jsonb")
    private String alertsByType; // JSON: {"PANIC_BUTTON": 20, "LOCATION_ANOMALY": 15}
    
    // Priority Distribution
    @Column(name = "alerts_by_priority", columnDefinition = "jsonb")
    private String alertsByPriority; // JSON: {"1": 5, "2": 10, "3": 8, "4": 4, "5": 3}
    
    // Time-based Metrics
    @Column(name = "peak_hour")
    private Integer peakHour; // 0-23
    
    @Column(name = "peak_day")
    private String peakDay; // Monday, Tuesday, etc.
    
    @Column(name = "average_daily_alerts", precision = 5, scale = 2)
    private Double averageDailyAlerts;
    
    // User Metrics
    @Column(name = "alerts_by_user_type", columnDefinition = "jsonb")
    private String alertsByUserType; // JSON: {"CUSTOMER": 25, "THERAPIST": 15}
    
    @Column(name = "repeat_alert_users")
    @Builder.Default
    private Integer repeatAlertUsers = 0;
    
    // Cost and Resource Metrics
    @Column(name = "security_company_calls")
    @Builder.Default
    private Integer securityCompanyCalls = 0;
    
    @Column(name = "estimated_cost_savings", precision = 10, scale = 2)
    @Builder.Default
    private Double estimatedCostSavings = 0.0;
    
    @Column(name = "response_cost", precision = 10, scale = 2)
    @Builder.Default
    private Double responseCost = 0.0;
    
    // System Performance Metrics
    @Column(name = "system_uptime_percentage", precision = 5, scale = 2)
    @Builder.Default
    private Double systemUptimePercentage = 100.0;
    
    @Column(name = "average_alert_detection_time", precision = 5, scale = 2)
    @Builder.Default
    private Double averageAlertDetectionTime = 0.0;
    
    @Column(name = "notification_success_rate", precision = 5, scale = 2)
    @Builder.Default
    private Double notificationSuccessRate = 100.0;
    
    @CreationTimestamp
    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;
    
    @Column(name = "metrics_start_date")
    private LocalDateTime metricsStartDate;
    
    @Column(name = "metrics_end_date")
    private LocalDateTime metricsEndDate;
    
    @Column(name = "is_aggregated")
    @Builder.Default
    private Boolean isAggregated = false;
    
    @Column(name = "aggregation_period", length = 20)
    private String aggregationPeriod; // "day", "week", "month", "year"
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @PrePersist
    protected void onCreate() {
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
        if (date == null) {
            date = LocalDateTime.now();
        }
    }
    
    // Helper methods
    public Double getStreamSuccessRate() {
        if (totalStreamsInitiated == 0) return 0.0;
        return (successfulStreams * 100.0) / totalStreamsInitiated;
    }
    
    public Double getAverageAlertsPerDay() {
        if (totalAlerts == 0) return 0.0;
        return totalAlerts / 30.0; // Assuming monthly metrics
    }
    
    public String getFormattedResolutionRate() {
        return String.format("%.2f%%", resolutionRate);
    }
    
    public String getFormattedFalseAlarmRate() {
        return String.format("%.2f%%", falseAlarmRate);
    }
    
    // Indexes for better query performance
    @Table(name = "security_metrics", indexes = {
        @Index(name = "idx_security_metrics_date", columnList = "date"),
        @Index(name = "idx_security_metrics_time_range", columnList = "time_range"),
        @Index(name = "idx_security_metrics_generated_at", columnList = "generated_at"),
        @Index(name = "idx_security_metrics_is_aggregated", columnList = "is_aggregated"),
        @Index(name = "idx_security_metrics_date_range", columnList = "metrics_start_date, metrics_end_date")
    })
    static class SecurityMetricsTableIndices {}
}