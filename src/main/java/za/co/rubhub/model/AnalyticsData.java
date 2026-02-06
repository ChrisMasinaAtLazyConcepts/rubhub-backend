package za.co.rubhub.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "analytics_data")
public class AnalyticsData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "time_range", length = 50)
    private String timeRange; // daily, weekly, monthly, quarterly, yearly
    
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
    
    // Embedded KPI data
    @ElementCollection
    @CollectionTable(name = "analytics_kpis", joinColumns = @JoinColumn(name = "analytics_id"))
    private List<KPI> kpis;
    
    @Embedded
    private RevenueData revenueData;
    
    @ElementCollection
    @CollectionTable(name = "analytics_therapist_performance", joinColumns = @JoinColumn(name = "analytics_id"))
    private List<TherapistPerformance> therapistPerformance;
    
    @ElementCollection
    @CollectionTable(name = "analytics_service_performance", joinColumns = @JoinColumn(name = "analytics_id"))
    private List<ServicePerformance> servicePerformance;
    
    @ElementCollection
    @CollectionTable(name = "analytics_geographic_performance", joinColumns = @JoinColumn(name = "analytics_id"))
    private List<GeographicPerformance> geographicPerformance;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        if (this.generatedAt == null) {
            this.generatedAt = LocalDateTime.now();
        }
        this.createdAt = LocalDateTime.now();
    }

    // Embedded classes
    @Embeddable
    public static class KPI {
        @Column(name = "label", length = 100)
        private String label;
        
        @Column(name = "value", precision = 19, scale = 4)
        private BigDecimal value;
        
        @Column(name = "change_value", precision = 19, scale = 4)
        private BigDecimal change;
        
        @Column(name = "change_type", length = 20)
        private String changeType; // positive, negative, neutral
        
        @Column(name = "format", length = 20)
        private String format; // currency, number, percentage
        
        @Column(name = "icon", length = 50)
        private String icon;

        // Getters and Setters
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        
        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
        
        public BigDecimal getChange() { return change; }
        public void setChange(BigDecimal change) { this.change = change; }
        
        public String getChangeType() { return changeType; }
        public void setChangeType(String changeType) { this.changeType = changeType; }
        
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        
        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
    }
    
    @Embeddable
    public static class RevenueData {
        @ElementCollection
        @CollectionTable(name = "analytics_daily_revenue", joinColumns = @JoinColumn(name = "analytics_id"))
        private List<DailyRevenue> dailyRevenue;
        
        @Column(name = "total_revenue", precision = 19, scale = 4)
        private BigDecimal totalRevenue;
        
        @Column(name = "total_bookings")
        private Integer totalBookings;
        
        @Column(name = "average_order_value", precision = 19, scale = 4)
        private BigDecimal averageOrderValue;

        @Embeddable
        public static class DailyRevenue {
            @Column(name = "revenue_date", length = 20)
            private String date;
            
            @Column(name = "revenue", precision = 19, scale = 4)
            private BigDecimal revenue;
            
            @Column(name = "bookings")
            private Integer bookings;
            
            @Column(name = "average_order", precision = 19, scale = 4)
            private BigDecimal averageOrder;

            // Getters and Setters
            public String getDate() { return date; }
            public void setDate(String date) { this.date = date; }
            
            public BigDecimal getRevenue() { return revenue; }
            public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
            
            public Integer getBookings() { return bookings; }
            public void setBookings(Integer bookings) { this.bookings = bookings; }
            
            public BigDecimal getAverageOrder() { return averageOrder; }
            public void setAverageOrder(BigDecimal averageOrder) { this.averageOrder = averageOrder; }
        }

        // Getters and Setters
        public List<DailyRevenue> getDailyRevenue() { return dailyRevenue; }
        public void setDailyRevenue(List<DailyRevenue> dailyRevenue) { this.dailyRevenue = dailyRevenue; }
        
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
        
        public Integer getTotalBookings() { return totalBookings; }
        public void setTotalBookings(Integer totalBookings) { this.totalBookings = totalBookings; }
        
        public BigDecimal getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }
    }
    
    @Embeddable
    public static class TherapistPerformance {
        @Column(name = "therapist_id")
        private Long therapistId;
        
        @Column(name = "name", length = 100)
        private String name;
        
        @Column(name = "sessions")
        private Integer sessions;
        
        @Column(name = "revenue", precision = 19, scale = 4)
        private BigDecimal revenue;
        
        @Column(name = "rating", precision = 3, scale = 2)
        private Double rating;
        
        @Column(name = "completion_rate", precision = 5, scale = 2)
        private Double completionRate;

        // Getters and Setters
        public Long getId() { return therapistId; }
        public void setTherapistId(Long therapistId) { this.therapistId = therapistId; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Integer getSessions() { return sessions; }
        public void setSessions(Integer sessions) { this.sessions = sessions; }
        
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        
        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
        
        public Double getCompletionRate() { return completionRate; }
        public void setCompletionRate(Double completionRate) { this.completionRate = completionRate; }
    }
    
    @Embeddable
    public static class ServicePerformance {
        @Column(name = "service", length = 100)
        private String service;
        
        @Column(name = "bookings")
        private Integer bookings;
        
        @Column(name = "revenue", precision = 19, scale = 4)
        private BigDecimal revenue;
        
        @Column(name = "average_rating", precision = 3, scale = 2)
        private Double averageRating;

        // Getters and Setters
        public String getService() { return service; }
        public void setService(String service) { this.service = service; }
        
        public Integer getBookings() { return bookings; }
        public void setBookings(Integer bookings) { this.bookings = bookings; }
        
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        
        public Double getAverageRating() { return averageRating; }
        public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    }
    
    @Embeddable
    public static class GeographicPerformance {
        @Column(name = "area", length = 100)
        private String area;
        
        @Column(name = "bookings")
        private Integer bookings;
        
        @Column(name = "revenue", precision = 19, scale = 4)
        private BigDecimal revenue;
        
        @Column(name = "growth", precision = 5, scale = 2)
        private Double growth;

        // Getters and Setters
        public String getArea() { return area; }
        public void setArea(String area) { this.area = area; }
        
        public Integer getBookings() { return bookings; }
        public void setBookings(Integer bookings) { this.bookings = bookings; }
        
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        
        public Double getGrowth() { return growth; }
        public void setGrowth(Double growth) { this.growth = growth; }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTimeRange() { return timeRange; }
    public void setTimeRange(String timeRange) { this.timeRange = timeRange; }
    
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    
    public List<KPI> getKpis() { return kpis; }
    public void setKpis(List<KPI> kpis) { this.kpis = kpis; }
    
    public RevenueData getRevenueData() { return revenueData; }
    public void setRevenueData(RevenueData revenueData) { this.revenueData = revenueData; }
    
    public List<TherapistPerformance> getTherapistPerformance() { return therapistPerformance; }
    public void setTherapistPerformance(List<TherapistPerformance> therapistPerformance) { this.therapistPerformance = therapistPerformance; }
    
    public List<ServicePerformance> getServicePerformance() { return servicePerformance; }
    public void setServicePerformance(List<ServicePerformance> servicePerformance) { this.servicePerformance = servicePerformance; }
    
    public List<GeographicPerformance> getGeographicPerformance() { return geographicPerformance; }
    public void setGeographicPerformance(List<GeographicPerformance> geographicPerformance) { this.geographicPerformance = geographicPerformance; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}