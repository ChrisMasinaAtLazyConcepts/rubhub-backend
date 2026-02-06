// AnalyticsController.java
package za.co.rubhub.controller;

import za.co.rubhub.model.AnalyticsData;
import za.co.rubhub.service.impl.AnalyticsService;
// import za.co.rubhub.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*") // Adjust based on your frontend URL
public class AnalyticsController {
    
    private AnalyticsService analyticsService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<AnalyticsData> getDashboardData(
            @RequestParam(defaultValue = "30d") String timeRange) {
        try {
            AnalyticsData data = analyticsService.getDashboardData(timeRange);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // @GetMapping("/kpis")
    // public ResponseEntity<Map<String, Object>> getKPIData(
    //         @RequestParam(defaultValue = "30d") String timeRange) {
    //     try {
    //         Map<String, Object> kpis = analyticsService.getKPIData(timeRange);
    //         return ResponseEntity.ok(kpis);
    //     } catch (Exception e) {
    //         return ResponseEntity.internalServerError().build();
    //     }
    // }
    
    @GetMapping("/revenue")
    public ResponseEntity<AnalyticsData.RevenueData> getRevenueData(
            @RequestParam(defaultValue = "30d") String timeRange) {
        try {
            AnalyticsData.RevenueData revenueData = analyticsService.getRevenueData(timeRange);
            return ResponseEntity.ok(revenueData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/therapists")
    public ResponseEntity<java.util.List<AnalyticsData.TherapistPerformance>> getTherapistPerformance(
            @RequestParam(defaultValue = "30d") String timeRange) {
        try {
            java.util.List<AnalyticsData.TherapistPerformance> performance = 
                analyticsService.getTherapistPerformance(timeRange);
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/services")
    public ResponseEntity<java.util.List<AnalyticsData.ServicePerformance>> getServicePerformance(
            @RequestParam(defaultValue = "30d") String timeRange) {
        try {
            java.util.List<AnalyticsData.ServicePerformance> performance = 
                analyticsService.getServicePerformance(timeRange);
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/geography")
    public ResponseEntity<java.util.List<AnalyticsData.GeographicPerformance>> getGeographicPerformance(
            @RequestParam(defaultValue = "30d") String timeRange) {
        try {
            java.util.List<AnalyticsData.GeographicPerformance> performance = 
                analyticsService.getGeographicPerformance(timeRange);
            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/generate")
    public ResponseEntity<Void> generateAnalyticsData(
            @RequestParam String timeRange) {
        try {
            analyticsService.generateAnalyticsData(timeRange);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/cleanup")
    public ResponseEntity<Void> cleanupOldAnalyticsData() {
        try {
            analyticsService.cleanupOldAnalyticsData();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}