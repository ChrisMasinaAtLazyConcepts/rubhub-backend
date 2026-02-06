package za.co.rubhub.repositories;

import za.co.rubhub.model.Geofence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeofenceRepository extends JpaRepository<Geofence, Long> {
    
    Optional<Geofence> findByName(String name);
    
    List<Geofence> findByType(String type);
    
    List<Geofence> findByIsActiveTrue();
    
    List<Geofence> findByTypeAndIsActiveTrue(String type);
    
    @Query("SELECT g FROM Geofence g WHERE g.isActive = true AND " +
           "ST_Distance_Sphere(point(g.centerLocation.lng, g.centerLocation.lat), point(:lng, :lat)) <= g.radius")
    List<Geofence> findGeofencesContainingPoint(@Param("lat") Double lat, @Param("lng") Double lng);
    
    @Query("SELECT g FROM Geofence g WHERE g.isActive = true AND g.type = 'no-service' AND " +
           "ST_Distance_Sphere(point(g.centerLocation.lng, g.centerLocation.lat), point(:lng, :lat)) <= g.radius")
    List<Geofence> findNoServiceZonesContainingPoint(@Param("lat") Double lat, @Param("lng") Double lng);
    
    @Query("SELECT g FROM Geofence g WHERE g.isActive = true AND g.type = 'high-risk' AND " +
           "ST_Distance_Sphere(point(g.centerLocation.lng, g.centerLocation.lat), point(:lng, :lat)) <= g.radius")
    List<Geofence> findHighRiskZonesContainingPoint(@Param("lat") Double lat, @Param("lng") Double lng);
    
    @Query("SELECT g FROM Geofence g WHERE g.isActive = true AND g.type = 'premium' AND " +
           "ST_Distance_Sphere(point(g.centerLocation.lng, g.centerLocation.lat), point(:lng, :lat)) <= g.radius")
    List<Geofence> findPremiumZonesContainingPoint(@Param("lat") Double lat, @Param("lng") Double lng);
    
    @Query(value = "SELECT g.* FROM geofences g WHERE g.is_active = true AND " +
                   "ST_Contains(ST_MakePolygon(ST_GeomFromText('LINESTRING(' || " +
                   "string_agg(gc.latitude || ' ' || gc.longitude, ', ') || ')')), " +
                   "ST_SetSRID(ST_MakePoint(:lng, :lat), 4326))", nativeQuery = true)
    List<Geofence> findPolygonGeofencesContainingPoint(@Param("lat") Double lat, @Param("lng") Double lng);
    
    @Query("SELECT COUNT(g) FROM Geofence g WHERE g.type = :type AND g.isActive = true")
    Long countByTypeAndActive(@Param("type") String type);
    
    @Query("SELECT g.type, COUNT(g) FROM Geofence g WHERE g.isActive = true GROUP BY g.type")
    List<Object[]> countByType();
    
    boolean existsByName(String name);
    
    boolean existsByNameAndType(String name, String type);
}