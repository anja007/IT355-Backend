package com.example.CS330_PZ.controller;

import com.example.CS330_PZ.DTO.ReviewsDTO;
import com.example.CS330_PZ.model.Reviews;
import com.example.CS330_PZ.service.ReviewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewsController {
    private final ReviewsService reviewsService;

    @GetMapping
    public ResponseEntity<?> getAllReviews(){
        return ResponseEntity.ok(reviewsService.getAllReviews());
    }

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
    
    @PostMapping("/createReview")
    public ResponseEntity<?> createReview(@RequestBody ReviewsDTO dto){
        return ResponseEntity.ok(reviewsService.createReviews(dto));
    }

    @GetMapping("/place/{placeId}")
    public ResponseEntity<?> getReviewsByPlaceId(@PathVariable("placeId") Integer placeId){
        return ResponseEntity.ok(reviewsService.getReviewsByPlaceId(placeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(@PathVariable Long id, @RequestBody ReviewsDTO dto){
        return ResponseEntity.ok(reviewsService.updateReview(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        boolean deleted = reviewsService.deleteReview(id);
        if (deleted) {
            return ResponseEntity.ok("Review deleted successfully.");
        } else {
            return ResponseEntity.status(403).body("You are not authorized to delete this review.");
        }
    }
}
