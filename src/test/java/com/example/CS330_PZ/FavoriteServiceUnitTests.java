package com.example.CS330_PZ;

import com.example.CS330_PZ.model.Place;
import com.example.CS330_PZ.model.User;
import com.example.CS330_PZ.repository.PlaceRepository;
import com.example.CS330_PZ.repository.UserRepository;
import com.example.CS330_PZ.service.FavoriteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private FavoriteService favoriteService;


    @Test
    public void addToFavoritesHappyFlow() {
        String username = "testUser";
        Long placeId = 1L;

        User user = new User();
        user.setUserId(1L);
        user.setUsername(username);
        user.setFavoritePlaces(new HashSet<>());

        Place place = new Place();
        place.setPlaceId(placeId);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(placeRepository.findById(placeId)).willReturn(Optional.of(place));

        favoriteService.addToFavorites(username, placeId);

        assertThat(user.getFavoritePlaces()).contains(place);
        verify(userRepository).save(user);
    }

    @Test
    public void addToFavoritesTestPlaceAlreadyAdded() {
        String username = "testUser";
        Long placeId = 1L;

        Place place = new Place();
        place.setPlaceId(placeId);

        User user = new User();
        user.setUserId(1L);
        user.setUsername(username);
        user.setFavoritePlaces(new HashSet<>(Set.of(place)));

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(placeRepository.findById(placeId)).willReturn(Optional.of(place));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> favoriteService.addToFavorites(username, placeId));

        assertThat(exception.getMessage()).isEqualTo("Place already in favorites");
    }
}
