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
        place.setPhotos(dto.getPhotos());


        return placeRepository.save(place);
    }

    public Optional<Place> getPlacesByPlaceId(Long placeId){
        return placeRepository.findById(placeId);
    }

    //zasto je iskorisceno za unit testove?
    public List<Place> getPlacesByCategoryId(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return placeRepository.findByCategoryId(category);
    }

    public List<Place> getTop5RatedPlaces() {
        return placeRepository.findTop5ByOrderByRatingDesc();
    }

    public Page<Place> getAllPlaces(Pageable pageable) {
        return placeRepository.findAll(pageable);
    }

    public Page<Place> searchByKeyword(String keyword, Pageable pageable) {
        return placeRepository.searchByKeyword(keyword, pageable);
    }

    /*
    public Page<Place> searchPlaces(String keyword, Pageable pageable) {
        return placeRepository.findByNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrAddressContainingIgnoreCase(keyword, keyword, keyword, pageable);
    }*/
    public Place updatePlace(Long placeId, PlaceDTO dto) {
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
