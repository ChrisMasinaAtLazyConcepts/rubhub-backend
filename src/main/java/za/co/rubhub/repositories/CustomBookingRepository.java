package za.co.rubhub.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class CustomBookingRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public List<Object[]> getRevenueByServiceType(LocalDate startDate, LocalDate endDate) {
        String query = "SELECT b.service_type, COUNT(b.id) as bookings, SUM(b.total_amount) as revenue " +
                      "FROM bookings b " +
                      "WHERE b.date BETWEEN :startDate AND :endDate " +
                      "AND b.status = 'COMPLETED' " +
                      "AND b.payment_status = 'paid' " +
                      "GROUP BY b.service_type " +
                      "ORDER BY revenue DESC";
        
        return entityManager.createNativeQuery(query)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
    
    public List<Object[]> getTherapistPerformanceMetrics(LocalDateTime startDate, LocalDateTime endDate) {
        String query = "SELECT " +
                      "t.id as therapist_id, " +
                      "u.first_name, " +
                      "u.last_name, " +
                      "COUNT(b.id) as total_sessions, " +
                      "SUM(b.total_amount) as total_revenue, " +
                      "AVG(t.rating) as average_rating, " +
                      "SUM(CASE WHEN b.status = 'COMPLETED' THEN 1 ELSE 0 END) / COUNT(b.id) * 100 as completion_rate " +
                      "FROM therapists t " +
                      "JOIN users u ON t.user_id = u.id " +
                      "LEFT JOIN bookings b ON t.id = b.therapist_id " +
                      "WHERE b.created_at BETWEEN :startDate AND :endDate " +
                      "GROUP BY t.id, u.first_name, u.last_name " +
                      "ORDER BY total_revenue DESC";
        
        return entityManager.createNativeQuery(query)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
    
    public BigDecimal getPlatformRevenue(LocalDate startDate, LocalDate endDate) {
        String query = "SELECT COALESCE(SUM(b.rubgo_service_fee), 0) " +
                      "FROM bookings b " +
                      "WHERE b.date BETWEEN :startDate AND :endDate " +
                      "AND b.status = 'COMPLETED' " +
                      "AND b.payment_status = 'paid'";
        
        return (BigDecimal) entityManager.createNativeQuery(query)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();
    }
}