package za.co.rubhub.repositories;

import za.co.rubhub.model.AnalyticsData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyticsRepository extends JpaRepository<AnalyticsData, Long> {
    
    Optional<AnalyticsData> findByTimeRange(String timeRange);
    
    @Query("SELECT a FROM AnalyticsData a WHERE a.timeRange = :timeRange AND a.generatedAt >= :generatedAt")
    Optional<AnalyticsData> findByTimeRangeAndGeneratedAtAfter(
            @Param("timeRange") String timeRange, 
            @Param("generatedAt") LocalDateTime generatedAt);
    
    List<AnalyticsData> findByGeneratedAtBetween(LocalDateTime start, LocalDateTime end);
    
    void deleteByGeneratedAtBefore(LocalDateTime date);
    
    // Additional JPA queries
    List<AnalyticsData> findByMetricType(String metricType);
    
    @Query("SELECT a FROM AnalyticsData a WHERE a.date BETWEEN :startDate AND :endDate")
    List<AnalyticsData> findByDateBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT a FROM AnalyticsData a WHERE a.isAggregated = true AND a.generatedAt >= :since")
    List<AnalyticsData> findRecentAggregatedData(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(a) FROM AnalyticsData a WHERE a.generatedAt >= :date")
    long countByGeneratedAtAfter(@Param("date") LocalDateTime date);
    
    @Query("SELECT a.timeRange, COUNT(a), MAX(a.generatedAt) FROM AnalyticsData a GROUP BY a.timeRange")
    List<Object[]> getAnalyticsSummaryByTimeRange();
    
    @Query("DELETE FROM AnalyticsData a WHERE a.generatedAt < :date AND a.isAggregated = false")
    int deleteOldNonAggregatedData(@Param("date") LocalDateTime date);
}