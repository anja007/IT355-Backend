package com.example.CS330_PZ.unit_tests;

import com.example.CS330_PZ.DTO.PlaceDTO;
import com.example.CS330_PZ.model.Category;
import com.example.CS330_PZ.model.Place;
import com.example.CS330_PZ.repository.CategoryRepository;
import com.example.CS330_PZ.repository.PlaceRepository;
import com.example.CS330_PZ.service.GeocodingService;
import com.example.CS330_PZ.service.PlaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceUnitTests {

    @Mock private PlaceRepository placeRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private GeocodingService geocodingService;

    @InjectMocks
    private PlaceService placeService;

    private PlaceDTO buildDto(Integer categoryId, Double rating) {
        PlaceDTO dto = new PlaceDTO();
        dto.setName("Mala Kafica");
        dto.setAddress("Knez Mihailova 1");
        dto.setCity("Beograd");
        dto.setCategoryId(categoryId);
        dto.setTags(List.of("wifi", "pets"));
        dto.setRating(rating);
        dto.setPhotos(List.of("photo1.jpg", "photo2.jpg"));
        return dto;
    }

    @Test
    void createPlace_HappyPath_DefaultsRatingToZero_WhenNull() {
        PlaceDTO dto = buildDto(1, null);

        Category cat = new Category();
        cat.setCategoryId(1L);
        cat.setCategoryName("Cafe");

        GeocodingService.LatLng coords = new GeocodingService.LatLng(44.7866, 20.4489);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(cat));
        when(geocodingService.geocodeAddress(anyString())).thenReturn(coords);

        when(placeRepository.save(any(Place.class))).thenAnswer(inv -> {
            Place p = inv.getArgument(0, Place.class);
            p.setPlaceId(123L);
            return p;
        });

        ArgumentCaptor<String> addrCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Place> placeCaptor = ArgumentCaptor.forClass(Place.class);

        Place saved = placeService.createPlace(dto);

        verify(geocodingService).geocodeAddress(addrCaptor.capture());
        assertThat(addrCaptor.getValue())
                .isEqualTo("Mala Kafica, Knez Mihailova 1, Beograd, Serbia");

        verify(placeRepository).save(placeCaptor.capture());
        Place toSave = placeCaptor.getValue();

        assertThat(toSave.getName()).isEqualTo("Mala Kafica");
        assertThat(toSave.getAddress()).isEqualTo("Knez Mihailova 1");
        assertThat(toSave.getCity()).isEqualTo("Beograd");
        assertThat(toSave.getCategoryId()).isSameAs(cat);
        assertThat(toSave.getTags()).containsExactly("wifi", "pets");
        assertThat(toSave.getPhotos()).containsExactly("photo1.jpg", "photo2.jpg");
        assertThat(toSave.getLat()).isEqualTo(44.7866);
        assertThat(toSave.getLng()).isEqualTo(20.4489);
        assertThat(toSave.getRating()).isEqualTo(0.0);

        assertThat(saved.getPlaceId()).isEqualTo(123L);
    }

    @Test
    void createPlace_UsesProvidedRating() {
        PlaceDTO dto = buildDto(2, 4.5);

        Category cat = new Category();
        cat.setCategoryId(2L);
        cat.setCategoryName("Restaurant");

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(cat));
        when(geocodingService.geocodeAddress(anyString()))
                .thenReturn(new GeocodingService.LatLng(43.32, 21.89));
        when(placeRepository.save(any(Place.class))).thenAnswer(inv -> inv.getArgument(0));

        Place saved = placeService.createPlace(dto);

        assertThat(saved.getRating()).isEqualTo(4.5);
        assertThat(saved.getCategoryId()).isSameAs(cat);
        verify(placeRepository, times(1)).save(any(Place.class));
    }

    @Test
    void createPlace_CategoryNotFound_ThrowsIllegalArgumentException() {
        PlaceDTO dto = buildDto(99, 5.0);

        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());
        when(geocodingService.geocodeAddress(anyString()))
                .thenReturn(new GeocodingService.LatLng(44.0, 20.0)); // biće pozvano pre bacanja

        assertThrows(IllegalArgumentException.class, () -> placeService.createPlace(dto));
        verify(placeRepository, never()).save(any());
    }

    @Test
    void createPlace_GeocodeFails_PropagatesException() {
        // Arrange
        PlaceDTO dto = buildDto(1, 3.0);
        when(geocodingService.geocodeAddress(anyString()))
                .thenThrow(new RuntimeException("Geocoding down"));

        assertThrows(RuntimeException.class, () -> placeService.createPlace(dto));
        verify(categoryRepository, never()).findById(anyLong()); // do geokodiranja se staje
        verify(placeRepository, never()).save(any());
    }

    @Test
    void createPlace_CopiesTagsAndPhotos() {
        PlaceDTO dto = buildDto(3, 2.0);

        Category cat = new Category();
        cat.setCategoryId(3L);
        cat.setCategoryName("Museum");

        when(categoryRepository.findById(3L)).thenReturn(Optional.of(cat));
        when(geocodingService.geocodeAddress(anyString()))
                .thenReturn(new GeocodingService.LatLng(45.0, 19.0));
        when(placeRepository.save(any(Place.class))).thenAnswer(inv -> inv.getArgument(0));

        Place saved = placeService.createPlace(dto);

        assertThat(saved.getTags()).containsExactly("wifi", "pets");
        assertThat(saved.getPhotos()).containsExactly("photo1.jpg", "photo2.jpg");
        assertThat(saved.getCategoryId()).isSameAs(cat);
    }
}
