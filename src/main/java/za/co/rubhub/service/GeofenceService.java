package za.co.rubhub.service;

import za.co.rubhub.model.*;
import za.co.rubhub.repositories.GeofenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GeofenceService {
    private final GeofenceRepository geofenceRepository;

    public Geofence createGeofence(Geofence geofence) {
        return geofenceRepository.save(geofence);
    }

    public List<Geofence> getAllGeofences() {
        return geofenceRepository.findAll();
    }

    public Optional<Geofence> getGeofenceById(Long id) {
        return geofenceRepository.findById(id);
    }

    public List<Geofence> getGeofencesByType(String type) {
        return geofenceRepository.findByType(type);
    }

    public Geofence updateGeofence(long id, Geofence geofence) {
        return geofenceRepository.findById(id).map(existing -> {
            existing.setName(geofence.getName());
            existing.setType(geofence.getType());
            existing.setLocation(geofence.getLocation());
            existing.setRadius(geofence.getRadius());
            existing.setDescription(geofence.getDescription());
            return geofenceRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Geofence not found"));
    }

    public void deleteGeofence(Long id) {
        geofenceRepository.deleteById(id);
    }

    public List<Geofence> checkLocation(Double lat, Double lng) {
        // Convert radius from meters to radians (assuming 100km radius for search)
        double radiusInRadians = 100000 / 6378137.0;
        return geofenceRepository.findGeofencesContainingPoint(lat, lng);
    }
}