package com.example.CS330_PZ.service;

import com.example.CS330_PZ.DTO.PlaceDTO;
import com.example.CS330_PZ.model.Category;
import com.example.CS330_PZ.model.Place;
import com.example.CS330_PZ.model.User;
import com.example.CS330_PZ.repository.CategoryRepository;
import com.example.CS330_PZ.repository.PlaceRepository;
import com.example.CS330_PZ.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final CategoryRepository categoryRepository;
    private final GeocodingService geocodingService;

    public Place createPlace(PlaceDTO dto) {
        Category category = categoryRepository.findById((long) dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Place place = new Place();
        place.setName(dto.getName());
        place.setAddress(dto.getAddress());
        place.setCity(dto.getCity());
        place.setCategoryId(category);
        place.setTags(dto.getTags());
        place.setRating(0.0);

        String fullAddress = place.getAddress().trim() + ", " + place.getCity().trim();
        GeocodingService.LatLng coords = geocodingService.geocodeAddress(fullAddress);

        if (coords == null) {
            String fallbackAddress = place.getAddress() + ", Serbia";
            coords = geocodingService.geocodeAddress(fallbackAddress);
        }

        if (coords != null) {
            System.out.println("✅ DOBIJENE KOORDINATE: " + coords.getLat() + ", " + coords.getLng());
            place.setLat(coords.getLat());
            place.setLng(coords.getLng());
        } else {
            // ❌ Nema rezultata ni nakon fallbacka
            throw new RuntimeException("❌ Neuspešno geokodiranje adrese: " + fullAddress);
        }


        return placeRepository.save(place);
    }


    public List<Place> getAllPlaces() {
        List<Place> places = placeRepository.getAllPlaces();
        return places;
    }

    /*
    public List<PlaceDTO> getAllPlaces() {
        List<Place> places = placeRepository.findAll();
        return places.stream()
                .map(p -> {
                    PlaceDTO dto = new PlaceDTO();
                    dto.setName(p.getName());
                    dto.setLocation(p.getLocation());
                    dto.setRating(p.getRating());
                    dto.setTags(p.getTags());
                    return dto;
                })
                .collect(Collectors.toList());
    }*/

    /*
    public Optional<Place> getPlaceById(Integer placeId){
        return placeRepository.getPlaceById(placeId);
    }
    */

    public Optional<Place> getPlacesByPlaceId(Long placeId){
       // Place place = placeRepository.findById(placeId).orElseThrow(() -> new RuntimeException("Place not found"));

        return placeRepository.findById(placeId);
    }

    public List<Place> getPlacesByCategoryId(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return placeRepository.findByCategoryId(category);
    }

    public List<Place> searchByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return placeRepository.findAll();
        }

        String lowerKeyword = keyword.toLowerCase();

        // Brza pretraga iz baze po name, city, address
        List<Place> resultsFromDb = placeRepository.searchByKeyword(lowerKeyword);

        // Dodatna filtracija po celim rečima i tagovima
        return resultsFromDb.stream()
                .filter(p -> {
                    boolean matchName = Arrays.asList(p.getName().toLowerCase().split("\\s+")).contains(lowerKeyword);
                    boolean matchCity = Arrays.asList(p.getCity().toLowerCase().split("\\s+")).contains(lowerKeyword);
                    boolean matchAddress = Arrays.asList(p.getAddress().toLowerCase().split("\\s+")).contains(lowerKeyword);

                    boolean matchTags = p.getTags() != null &&
                            p.getTags().stream()
                                    .map(String::toLowerCase)
                                    .anyMatch(t -> t.contains(lowerKeyword));

                    return matchName || matchCity || matchAddress || matchTags;
                })
                .toList();
    }

    public List<Place> getTop5RatedPlaces() {
        return placeRepository.findTop5ByOrderByRatingDesc();
    }

    public Page<Place> getAllPlaces(Pageable pageable) {
        return placeRepository.findAll(pageable);
    }

    public Page<Place> searchPlaces(String keyword, Pageable pageable) {
        return placeRepository.findByNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrAddressContainingIgnoreCase(keyword, keyword, keyword, pageable);
    }

}
