package com.example.CS330_PZ.controller;

import com.example.CS330_PZ.model.Place;
import com.example.CS330_PZ.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Set;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{placeId}")
    public ResponseEntity<?> addToFavorites(@PathVariable Long placeId, Principal principal) {
        favoriteService.addToFavorites(principal.getName(), placeId);
        return ResponseEntity.ok("Place added to favorites");
    }

    @GetMapping
    public ResponseEntity<Set<Place>> getFavorites(Principal principal) {
        return ResponseEntity.ok(favoriteService.getFavorites(principal.getName()));
    }

    @DeleteMapping("/{placeId}")
    public ResponseEntity<?> removeFromFavorites(@PathVariable Long placeId, Principal principal) {
        favoriteService.removeFromFavorites(principal.getName(), placeId);
        return ResponseEntity.ok("Place removed from favorites");
    }
}
