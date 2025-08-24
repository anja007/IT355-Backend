package com.example.CS330_PZ.controller;

import com.example.CS330_PZ.DTO.ReviewsDTO;
import com.example.CS330_PZ.model.Reviews;
import com.example.CS330_PZ.service.ReviewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewsController {
    private final ReviewsService reviewsService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllReviews(){
        return ResponseEntity.ok(reviewsService.getAllReviews());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/myReviews")
    public ResponseEntity<List<Reviews>> getMyReviews(Authentication authentication) {
        String username = authentication.getName();

        List<Reviews> reviews = reviewsService.getReviewsForUser(username);
        return ResponseEntity.ok(reviews);
    }


    @GetMapping("/review/{id}")
    public ResponseEntity<?> getReviewById(@PathVariable Long id) {
        return ResponseEntity.ok(reviewsService.getReviewById(id));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/createReview")
    public ResponseEntity<?> createReview(@Valid @RequestBody ReviewsDTO dto){
        try {
            return ResponseEntity.ok(reviewsService.createReviews(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<?> getReviewsByPlaceId(@PathVariable("placeId") Integer placeId){
        return ResponseEntity.ok(reviewsService.getReviewsByPlaceId(placeId));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody ReviewsDTO dto){
        return ResponseEntity.ok(
                Map.of("message", "Review updated successfully.",
                        "review", reviewsService.updateReview(id, dto))
        );
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        reviewsService.deleteReview(id);
        return ResponseEntity.ok(Map.of("message", "Review deleted successfully."));
    }
}
