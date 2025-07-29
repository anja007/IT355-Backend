package com.example.CS330_PZ.service;

import com.example.CS330_PZ.model.Place;
import com.example.CS330_PZ.model.User;
import com.example.CS330_PZ.repository.PlaceRepository;
import com.example.CS330_PZ.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    public void addToFavorites(String username, Long placeId) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        Place place = placeRepository.findById(placeId).orElseThrow(() -> new RuntimeException("Place not found"));

        user.getFavoritePlaces().add(place);
        userRepository.save(user);
    }

    public Set<Place> getFavorites(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        return user.getFavoritePlaces();
    }

    public void removeFromFavorites(String username, Long placeId){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        user.getFavoritePlaces().removeIf(place -> place.getPlaceId().equals(placeId));
        userRepository.save(user);
    }
}
