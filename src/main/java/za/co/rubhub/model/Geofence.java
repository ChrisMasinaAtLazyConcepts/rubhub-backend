package za.co.rubhub.model;

import lombok.*;
import za.co.rubhub.model.Location;
import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "geofences")
@Getter
@Setter
@AllArgsConstructor
public class Geofence {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    
    @Column(name = "type", length = 20, nullable = false)
    private String type; // no-service, high-risk, premium, safe-zone
    
    @ElementCollection
    @CollectionTable(name = "geofence_Location", joinColumns = @JoinColumn(name = "geofence_id"))
    private List<Location> Location = new ArrayList<>();
    
    @Column(name = "radius", precision = 10, scale = 2)
    private Double radius;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @Embedded
    private Location centerLocation;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private java.time.LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.LocalDateTime.now();
        this.updatedAt = java.time.LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = java.time.LocalDateTime.now();
    }

}