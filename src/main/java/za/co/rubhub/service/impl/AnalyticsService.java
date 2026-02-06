// AnalyticsServiceImpl.java
package za.co.rubhub.service.impl;

import za.co.rubhub.model.*;
import za.co.rubhub.repositories.AnalyticsRepository;
import za.co.rubhub.repositories.BookingRepository;
import za.co.rubhub.repositories.UserRepository;
import za.co.rubhub.repositories.TherapistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final AnalyticsRepository analyticsRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TherapistRepository therapistRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d");
    
    public AnalyticsData getDashboardData(String timeRange) {
        log.info("Fetching dashboard data for time range: {}", timeRange);
        
        // Try to get cached analytics data first
        Optional<AnalyticsData> cachedData = analyticsRepository
                .findByTimeRangeAndGeneratedAtAfter(timeRange, LocalDateTime.now().minusHours(1));
        
        if (cachedData.isPresent()) {
            log.info("Returning cached analytics data");
            return cachedData.get();
        }
        
        // Generate fresh data if cache is stale or doesn't exist
        log.info("Generating fresh analytics data");
        return generateAndSaveAnalyticsData(timeRange);
    }
    
    public Map<String, Object> getKPIData(String timeRange) {
        log.info("Fetching KPI data for time range: {}", timeRange);
        
        LocalDateTime[] dateRange = calculateDateRange(timeRange);
        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];
        
        // Get previous period for comparison
        LocalDateTime[] previousDateRange = calculatePreviousDateRange(timeRange);
        LocalDateTime previousStartDate = previousDateRange[0];
        LocalDateTime previousEndDate = previousDateRange[1];
        
        Map<String, Object> kpis = new HashMap<>();
        
        try {
            // Total Users KPI
            long currentUsers = userRepository.countByCreatedAtBetween(startDate, endDate);
            long previousUsers = userRepository.countByCreatedAtBetween(previousStartDate, previousEndDate);
            double userGrowth = calculateGrowthRate(currentUsers, previousUsers);
            
            // kpis.put("totalUsers", Map.of(
            //     "value", currentUsers,
            //     "change", userGrowth,
            //     "changeType", userGrowth >= 0 ? "positive" : "negative",
            //     "format", "number",
            //     "icon", "users"
            // ));
            
            // Total Revenue KPI
            List<Booking> currentBookings = bookingRepository.findCompletedPaidBookingsInDateRange(startDate, endDate);
            List<Booking> previousBookings = bookingRepository.findCompletedPaidBookingsInDateRange(previousStartDate, previousEndDate);
            
            BigDecimal currentRevenue = currentBookings.stream()
                    .map(Booking::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal previousRevenue = previousBookings.stream()
                    .map(Booking::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            double revenueGrowth = calculateGrowthRate(currentRevenue.doubleValue(), previousRevenue.doubleValue());
            
            // kpis.put("totalRevenue", Map.of(
            //     "value", currentRevenue,
            //     "change", revenueGrowth,
            //     "changeType", revenueGrowth >= 0 ? "positive" : "negative",
            //     "format", "currency",
            //     "icon", "money"
            // ));
            
            // Appointments KPI
            int currentAppointments = currentBookings.size();
            int previousAppointments = previousBookings.size();
            double appointmentGrowth = calculateGrowthRate(currentAppointments, previousAppointments);
            
            // kpis.put("appointments", Map.of(
            //     "value", currentAppointments,
            //     "change", appointmentGrowth,
            //     "changeType", appointmentGrowth >= 0 ? "positive" : "negative",
            //     "format", "number",
            //     "icon", "calendar"
            // ));
            
            // Growth Rate KPI (average of other growth rates)
            double averageGrowth = (userGrowth + revenueGrowth + appointmentGrowth) / 3;
            
            // kpis.put("growthRate", Map.of(
            //     "value", averageGrowth,
            //     "change", 0.0, // This would need historical growth rate data
            //     "changeType", averageGrowth >= 0 ? "positive" : "negative",
            //     "format", "percentage",
            //     "icon", "chart"
            // ));
            
        } catch (Exception e) {
            log.error("Error calculating KPI data: {}", e.getMessage(), e);
            // Return default values in case of error
            kpis.put("error", "Unable to calculate KPIs");
        }
        
        return kpis;
    }
    
    public AnalyticsData.RevenueData getRevenueData(String timeRange) {
        log.info("Fetching revenue data for time range: {}", timeRange);
        
        LocalDateTime[] dateRange = calculateDateRange(timeRange);
        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];
        
        List<Booking> bookings = bookingRepository.findCompletedPaidBookingsInDateRange(startDate, endDate);
        
        AnalyticsData.RevenueData revenueData = new AnalyticsData.RevenueData();
        
 // Calculate daily revenue - filter out bookings without completion date
        Map<Object,List<Booking>> bookingsByDate = bookings.stream()
            .filter(booking -> booking.getCompletedAt() != null)
            .collect(Collectors.groupingBy(booking -> 
                ((LocalDateTime) booking.getCompletedAt()).toLocalDate()
                  ));
        
        List<AnalyticsData.RevenueData.DailyRevenue> dailyRevenue = bookingsByDate.entrySet().stream()
                .sorted()
                .map(entry -> {
                    LocalDate date = (LocalDate) entry.getKey();
                    List<Booking> dayBookings = entry.getValue();
                    
                    BigDecimal dayRevenue = dayBookings.stream()
                            .map(Booking::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    BigDecimal averageOrder = dayBookings.isEmpty() ? BigDecimal.ZERO :
                            dayRevenue.divide(BigDecimal.valueOf(dayBookings.size()), 2, RoundingMode.HALF_UP);
                    
                    AnalyticsData.RevenueData.DailyRevenue daily = new AnalyticsData.RevenueData.DailyRevenue();
                    daily.setDate(date.format(DATE_FORMATTER));
                    daily.setRevenue(dayRevenue);
                    daily.setBookings(dayBookings.size());
                    daily.setAverageOrder(averageOrder);
                    
                    return daily;
                })
                .collect(Collectors.toList());
        
        revenueData.setDailyRevenue(dailyRevenue);
        
        // Calculate totals
        BigDecimal totalRevenue = bookings.stream()
                .map(Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageOrderValue = bookings.isEmpty() ? BigDecimal.ZERO :
                totalRevenue.divide(BigDecimal.valueOf(bookings.size()), 2, RoundingMode.HALF_UP);
        
        revenueData.setTotalRevenue(totalRevenue);
        revenueData.setTotalBookings(bookings.size());
        revenueData.setAverageOrderValue(averageOrderValue);
        
        return revenueData;
    }
    
    public List<AnalyticsData.TherapistPerformance> getTherapistPerformance(String timeRange) {
        log.info("Fetching therapist performance data for time range: {}", timeRange);
        
        LocalDateTime[] dateRange = calculateDateRange(timeRange);
        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];
        
        List<Booking> bookings = bookingRepository.findCompletedPaidBookingsInDateRange(startDate, endDate);
        
        // Group bookings by therapist and calculate performance metrics
        Map<Object,List<Booking>> bookingsByTherapist = bookings.stream()
                .filter(booking -> booking.getTherapist() != null)
                .collect(Collectors.groupingBy(booking -> booking.getTherapist().getId()));
        
        return bookingsByTherapist.entrySet().stream()
                .map(entry -> {
                    String therapistId = (String) entry.getKey();
                    List<Booking> therapistBookings = entry.getValue();
                    
                    // Calculate metrics
                    int sessions = therapistBookings.size();
                    BigDecimal revenue = therapistBookings.stream()
                            .map(Booking::getTherapistEarnings)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    // Calculate average rating (you'd need to implement this based on your rating system)
                    double averageRating = calculateTherapistAverageRating(therapistId);
                    
                    // Calculate completion rate (you'd need to implement this based on your booking status history)
                    double completionRate = calculateTherapistCompletionRate(therapistId, therapistBookings);
                    
                    AnalyticsData.TherapistPerformance performance = new AnalyticsData.TherapistPerformance();
                    // performance.setTherapistId(therapistId);
                    performance.setName(getTherapistName(therapistId));
                    performance.setSessions(sessions);
                    performance.setRevenue(revenue);
                    performance.setRating(averageRating);
                    performance.setCompletionRate(completionRate);
                    
                    return performance;
                })
                .sorted((a, b) -> b.getRevenue().compareTo(a.getRevenue())) // Sort by revenue descending
                .limit(10) // Top 10 therapists
                .collect(Collectors.toList());
    }
    
    public List<AnalyticsData.ServicePerformance> getServicePerformance(String timeRange) {
        log.info("Fetching service performance data for time range: {}", timeRange);
        
        LocalDateTime[] dateRange = calculateDateRange(timeRange);
        LocalDateTime startDate = dateRange[0];
        LocalDateTime endDate = dateRange[1];
        
        List<Booking> bookings = bookingRepository.findCompletedPaidBookingsInDateRange(startDate, endDate);
        
    // Group bookings by service type and calculate performance metrics
        Map<MassageServiceType, List<Booking>> bookingsByService = bookings.stream()
                .collect(Collectors.groupingBy(Booking::getServiceType));

        return bookingsByService.entrySet().stream()
                .map(entry -> {
                    MassageServiceType service = entry.getKey();
                    List<Booking> serviceBookings = entry.getValue();
                    
                    int bookingCount = serviceBookings.size();
                    BigDecimal revenue = serviceBookings.stream()
                            .map(Booking::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    
                    // Calculate average rating for this service
                    double averageRating = calculateServiceAverageRating(serviceBookings);
                    
                    AnalyticsData.ServicePerformance performance = new AnalyticsData.ServicePerformance();
                    performance.setBookings(bookingCount);
                    performance.setRevenue(revenue);
                    performance.setAverageRating(averageRating);
                    
                    return performance;
                })
                .sorted((a, b) -> b.getRevenue().compareTo(a.getRevenue())) // Sort by revenue descending
                .collect(Collectors.toList());
    }
    
    public List<AnalyticsData.GeographicPerformance> getGeographicPerformance(String timeRange) {
        log.info("Fetching geographic performance data for time range: {}", timeRange);
        
        // This would require additional geographic data in your Booking model
        // For now, returning mock data - you'll need to implement based on your actual data structure
        
        List<AnalyticsData.GeographicPerformance> geographicData = new ArrayList<>();
        
        // Example implementation - you'll need to adapt this to your actual geographic data
        Map<String, List<Booking>> bookingsByArea = new HashMap<>(); // Implement this based on your data
        
        for (Map.Entry<String, List<Booking>> entry : bookingsByArea.entrySet()) {
            String area = entry.getKey();
            List<Booking> areaBookings = entry.getValue();
            
            int bookings = areaBookings.size();
            BigDecimal revenue = areaBookings.stream()
                    .map(Booking::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Calculate growth (you'd need historical data for this)
            double growth = calculateAreaGrowth(area, timeRange);
            
            AnalyticsData.GeographicPerformance performance = new AnalyticsData.GeographicPerformance();
            performance.setArea(area);
            performance.setBookings(bookings);
            performance.setRevenue(revenue);
            performance.setGrowth(growth);
            
            geographicData.add(performance);
        }
        
        return geographicData.stream()
                .sorted((a, b) -> b.getRevenue().compareTo(a.getRevenue()))
                .collect(Collectors.toList());
    }
    
    public void generateAnalyticsData(String timeRange) {
        log.info("Generating analytics data for time range: {}", timeRange);
        generateAndSaveAnalyticsData(timeRange);
    }
    
    public void cleanupOldAnalyticsData() {
        log.info("Cleaning up old analytics data");
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);
        analyticsRepository.deleteByGeneratedAtBefore(cutoffDate);
    }
    
    // Private helper methods
    private AnalyticsData generateAndSaveAnalyticsData(String timeRange) {
        AnalyticsData analyticsData = new AnalyticsData();
        analyticsData.setTimeRange(timeRange);
        analyticsData.setGeneratedAt(LocalDateTime.now());
        
        // Generate all data components
        Map<String, Object> kpiData = getKPIData(timeRange);
        AnalyticsData.RevenueData revenueData = getRevenueData(timeRange);
        List<AnalyticsData.TherapistPerformance> therapistPerformance = getTherapistPerformance(timeRange);
        List<AnalyticsData.ServicePerformance> servicePerformance = getServicePerformance(timeRange);
        List<AnalyticsData.GeographicPerformance> geographicPerformance = getGeographicPerformance(timeRange);
        
        // Convert KPI data to proper format
        List<AnalyticsData.KPI> kpis = convertKpiData(kpiData);
        
        analyticsData.setKpis(kpis);
        analyticsData.setRevenueData(revenueData);
        analyticsData.setTherapistPerformance(therapistPerformance);
        analyticsData.setServicePerformance(servicePerformance);
        analyticsData.setGeographicPerformance(geographicPerformance);
        
        // Save to cache
        return analyticsRepository.save(analyticsData);
    }
    
    private List<AnalyticsData.KPI> convertKpiData(Map<String, Object> kpiData) {
        List<AnalyticsData.KPI> kpis = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : kpiData.entrySet()) {
            if (entry.getValue() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> kpiMap = (Map<String, Object>) entry.getValue();
                
                AnalyticsData.KPI kpi = new AnalyticsData.KPI();
                kpi.setLabel(getKpiLabel(entry.getKey()));
                kpi.setValue(new BigDecimal(kpiMap.get("value").toString()));
                kpi.setChange(new BigDecimal(kpiMap.get("change").toString()));
                kpi.setChangeType((String) kpiMap.get("changeType"));
                kpi.setFormat((String) kpiMap.get("format"));
                kpi.setIcon((String) kpiMap.get("icon"));
                
                kpis.add(kpi);
            }
        }
        
        return kpis;
    }
    
    private String getKpiLabel(String key) {
        switch (key) {
            case "totalUsers": return "Total Users";
            case "totalRevenue": return "Total Revenue";
            case "appointments": return "Appointments";
            case "growthRate": return "Growth Rate";
            default: return key;
        }
    }
    
    private LocalDateTime[] calculateDateRange(String timeRange) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate;
        
        switch (timeRange) {
            case "7d":
                startDate = endDate.minusDays(7);
                break;
            case "30d":
                startDate = endDate.minusDays(30);
                break;
            case "90d":
                startDate = endDate.minusDays(90);
                break;
            case "1y":
                startDate = endDate.minusYears(1);
                break;
            default:
                startDate = endDate.minusDays(30); // Default to 30 days
        }
        
        return new LocalDateTime[]{startDate, endDate};
    }
    
    private LocalDateTime[] calculatePreviousDateRange(String timeRange) {
        LocalDateTime[] currentRange = calculateDateRange(timeRange);
        long daysBetween = java.time.Duration.between(currentRange[0], currentRange[1]).toDays();
        
        LocalDateTime previousEndDate = currentRange[0].minusDays(1);
        LocalDateTime previousStartDate = previousEndDate.minusDays(daysBetween);
        
        return new LocalDateTime[]{previousStartDate, previousEndDate};
    }
    
    private double calculateGrowthRate(double current, double previous) {
        if (previous == 0) return current > 0 ? 100.0 : 0.0;
        return ((current - previous) / previous) * 100;
    }
    
    // These methods need to be implemented based on your actual data model
    private double calculateTherapistAverageRating(String therapistId) {
        // Implement based on your rating system
        return 4.5 + (new Random().nextDouble() * 0.5); // Mock data
    }
    
    private double calculateTherapistCompletionRate(String therapistId, List<Booking> bookings) {
        // Implement based on your completion tracking
        long completed = bookings.stream()
                .filter(b -> "COMPLETED".equals(b.getStatus().toString()))
                .count();
        return bookings.isEmpty() ? 0 : (double) completed / bookings.size() * 100;
    }
    
    private double calculateServiceAverageRating(List<Booking> serviceBookings) {
        // Implement based on your rating system
        return 4.5 + (new Random().nextDouble() * 0.5); // Mock data
    }
    
    private double calculateAreaGrowth(String area, String timeRange) {
        // Implement based on your historical data
        return 5.0 + (new Random().nextDouble() * 15.0); // Mock data
    }
    
    private String getTherapistName(String therapistId) {
        // Implement to get therapist name from repository
        return "Therapist " + therapistId; // Mock data
    }
}