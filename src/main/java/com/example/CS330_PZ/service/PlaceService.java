package com.example.CS330_PZ.service;

import com.example.CS330_PZ.DTO.PlaceDTO;
import com.example.CS330_PZ.model.Category;
import com.example.CS330_PZ.model.Place;
import com.example.CS330_PZ.repository.CategoryRepository;
import com.example.CS330_PZ.repository.PlaceRepository;
import com.opencagedata.jopencage.JOpenCageGeocoder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final CategoryRepository categoryRepository;
    private final GeocodingService geocodingService;

    @Value("${opencage.api.key}")
    private String apiKey;

    private JOpenCageGeocoder geocoder;

    @PostConstruct
    public void init() {
        this.geocoder = new JOpenCageGeocoder(apiKey);
    }

    public Place createPlace(PlaceDTO dto) {
        String fullAddress = String.format("%s, %s, %s, Serbia", dto.getName(), dto.getAddress(), dto.getCity());
        GeocodingService.LatLng coords = geocodingService.geocodeAddress(fullAddress);

        Category category = categoryRepository.findById((long) dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Place place = new Place();
        place.setName(dto.getName());
        place.setAddress(dto.getAddress());
        place.setCity(dto.getCity());
        place.setCategoryId(category);
        place.setTags(dto.getTags());
        place.setRating(dto.getRating() != null ? dto.getRating() : 0.0);
        place.setLat(coords.lat);
        place.setLng(coords.lng);

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

        List<Place> resultsFromDb = placeRepository.searchByKeyword(lowerKeyword);

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
    public Place updatePlace(Long placeId, PlaceDTO dto) {
        System.out.println("service");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new AccessDeniedException("Only admins can update places.");
        }

        Place existingPlace = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));

        existingPlace.setName(dto.getName());
        existingPlace.setAddress(dto.getAddress());
        existingPlace.setCity(dto.getCity());
        existingPlace.setTags(dto.getTags());

        Category category = categoryRepository.findById((long) dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        existingPlace.setCategoryId(category);

        String fullAddress = String.format("%s, %s, Serbia", dto.getAddress(), dto.getCity());
        GeocodingService.LatLng coords = geocodingService.geocodeAddress(fullAddress);
        existingPlace.setLat(coords.lat);
        existingPlace.setLng(coords.lng);

        return placeRepository.save(existingPlace);
    }

    public void deletePlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));

        placeRepository.delete(place);
    }


}
