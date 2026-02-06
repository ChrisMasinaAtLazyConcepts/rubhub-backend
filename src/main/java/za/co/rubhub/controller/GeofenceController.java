package za.co.rubhub.controller;

import za.co.rubhub.model.Geofence;
import za.co.rubhub.service.GeofenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/geofences")
@RequiredArgsConstructor
public class GeofenceController {
    private final GeofenceService geofenceService;

    @PostMapping
    public ResponseEntity<Geofence> createGeofence(@RequestBody Geofence geofence) {
        return ResponseEntity.ok(geofenceService.createGeofence(geofence));
    }

    @GetMapping
    public ResponseEntity<List<Geofence>> getAllGeofences() {
        return ResponseEntity.ok(geofenceService.getAllGeofences());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Geofence> getGeofenceById(@PathVariable Long id) {
        return geofenceService.getGeofenceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Geofence>> getGeofencesByType(@PathVariable String type) {
        return ResponseEntity.ok(geofenceService.getGeofencesByType(type));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Geofence> updateGeofence(@PathVariable Long id, @RequestBody Geofence geofence) {
        return ResponseEntity.ok(geofenceService.updateGeofence(id, geofence));
    }


    @GetMapping("/check-location")
    public ResponseEntity<List<Geofence>> checkLocation(
            @RequestParam Double lat,
            @RequestParam Double lng) {
        return ResponseEntity.ok(geofenceService.checkLocation(lat, lng));
    }
}