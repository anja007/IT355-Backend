package com.example.CS330_PZ.controller;

import com.example.CS330_PZ.DTO.PlaceDTO;
import com.example.CS330_PZ.model.Place;
import com.example.CS330_PZ.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@EnableMethodSecurity
public class PlaceController {

    private final PlaceService placeService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/add")
    public ResponseEntity<?> addPlace(@RequestBody PlaceDTO place) {
        try {
            return ResponseEntity.ok(placeService.createPlace(place));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());

        } 
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/update/{placeId}")
    public ResponseEntity<?> updatePlace(@PathVariable Long placeId, @RequestBody PlaceDTO dto){
        return ResponseEntity.ok(placeService.updatePlace(placeId, dto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/delete/{placeId}")
    public ResponseEntity<?> deletePlace(@PathVariable Long placeId) {
        placeService.deletePlace(placeId);
        return ResponseEntity.ok("Place deleted successfully.");
    }


    @GetMapping("/all")
    public ResponseEntity<?> getPlaces() {
        return ResponseEntity.ok(placeService.getAllPlaces());
    }

    @GetMapping
    public ResponseEntity<Page<Place>> getAllPlaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Place> placesPage = placeService.getAllPlaces(pageable);
        return ResponseEntity.ok(placesPage);
    }

    @GetMapping("/search/paginated")
    public ResponseEntity<Page<Place>> searchPlacesPaginated(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Place> result = placeService.searchPlaces(keyword, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<?> showPlaceDetails(@PathVariable("placeId") Long placeId){
        return ResponseEntity.ok(placeService.getPlacesByPlaceId(placeId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Place>> getPlacesByCategoryId(@PathVariable("categoryId") Long categoryId){
        List<Place> places = placeService.getPlacesByCategoryId(categoryId);
        return ResponseEntity.ok(places);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByKeyword(@RequestParam String keyword){
        System.out.println("here");
        List<Place> results = placeService.searchByKeyword(keyword.trim());

        if(results.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No results for searched term");
        }

        return ResponseEntity.ok(results);
    }

    @GetMapping("/place/{id}")
    public ResponseEntity<?> getPlaceById(@PathVariable Long id) {
        return ResponseEntity.ok(placeService.getPlacesByPlaceId(id));
    }

    //za prikaz na pocetnoj
    @GetMapping("/top-rated")
    public ResponseEntity<List<Place>> getTopRatedPlaces() {
        List<Place> topPlaces = placeService.getTop5RatedPlaces();
        return ResponseEntity.ok(topPlaces);
    }

}
