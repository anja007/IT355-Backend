package com.example.CS330_PZ;

import com.example.CS330_PZ.model.Category;
import com.example.CS330_PZ.model.Place;
import com.example.CS330_PZ.repository.CategoryRepository;
import com.example.CS330_PZ.repository.PlaceRepository;
import com.example.CS330_PZ.service.GeocodingService;
import com.example.CS330_PZ.service.PlaceService;
import jakarta.persistence.Table;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PlaceServiceUnitTests {

    @Test
    public void getPlacesByCategoryIdHappyFlow(){
        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        PlaceRepository placeRepository = mock(PlaceRepository.class);
        GeocodingService geocodingService = mock(GeocodingService.class);

        PlaceService placeService = new PlaceService(placeRepository, categoryRepository, geocodingService);

        Long categoryId = 1L;
        Category category = new Category();
        category.setCategoryId(categoryId);

        Place place1 = new Place();
        Place place2 = new Place();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(placeRepository.findByCategoryId(category)).thenReturn(List.of(place1, place2));

        List<Place> result = placeService.getPlacesByCategoryId(categoryId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(categoryRepository).findById(categoryId);
        verify(placeRepository).findByCategoryId(category);
    }

    @Test
    public void getPlacesByCategoryIdNotFound(){
        Long categoryId = 1L;

        CategoryRepository categoryRepository = mock(CategoryRepository.class);
        PlaceRepository placeRepository = mock(PlaceRepository.class);
        GeocodingService geocodingService = mock(GeocodingService.class);

        PlaceService placeService = new PlaceService(placeRepository, categoryRepository, geocodingService);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            placeService.getPlacesByCategoryId(categoryId);
        });

        assertEquals("Category not found", exception.getMessage());
        verify(categoryRepository).findById(categoryId);
        verify(placeRepository, never()).findByCategoryId(any());
    }
}
