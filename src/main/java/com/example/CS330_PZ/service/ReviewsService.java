package com.example.CS330_PZ.service;

import com.example.CS330_PZ.DTO.ReviewResponseDTO;
import com.example.CS330_PZ.DTO.ReviewsDTO;
import com.example.CS330_PZ.model.Place;
import com.example.CS330_PZ.model.Reviews;
import com.example.CS330_PZ.model.User;
import com.example.CS330_PZ.repository.PlaceRepository;
import com.example.CS330_PZ.repository.ReviewsRepository;
import com.example.CS330_PZ.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewsService {
    private final ReviewsRepository reviewsRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    private void updatePlaceRating(Place place) {
        List<Reviews> reviews = reviewsRepository.getReviewsByPlaceId(Math.toIntExact(place.getPlaceId()));
        double avg = reviews.stream()
                .mapToInt(Reviews::getRating)
                .average()
                .orElse(0.0);
        place.setRating(avg);
        placeRepository.save(place);
    }
    @Transactional
    public ReviewResponseDTO createReviews(ReviewsDTO dto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Place place = placeRepository.findById((long) dto.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));

        Reviews reviews = new Reviews();
        reviews.setUserId(user);
        reviews.setPlaceId(place);

        //validatorService.validateRating(dto.getRating());
        reviews.setRating(dto.getRating());
        reviews.setComment(dto.getComment());
        reviews.setCreatedAt(LocalDateTime.now());

        Reviews saved = reviewsRepository.save(reviews);
        updatePlaceRating(saved.getPlaceId());

        return getReviewResponseDTOS(List.of(saved)).get(0);
    }

    public List<Reviews> getAllReviews() {
        return reviewsRepository.getAllReviews();
    }

    public List<ReviewResponseDTO> getReviewsByPlaceId(Integer placeId){
        List<Reviews> reviews = reviewsRepository.getReviewsByPlaceId(placeId);

        return getReviewResponseDTOS(reviews);

    }
    public List<Reviews> getReviewsForUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        return reviewsRepository.getReviewsByUserId(Math.toIntExact(user.getUserId()));
    }

    public ReviewResponseDTO getReviewById(Long id) {
        Reviews review = reviewsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        return getReviewResponseDTOS(List.of(review)).get(0);
    }

    private List<ReviewResponseDTO> getReviewResponseDTOS(List<Reviews> reviews) {
        return reviews.stream().map(review -> {
            ReviewResponseDTO dto = new ReviewResponseDTO();
            dto.setReviewId(Math.toIntExact(review.getReviewId()));
            dto.setComment(review.getComment());
            dto.setRating(review.getRating());
            dto.setCreatedAt(String.valueOf(review.getCreatedAt()));

            dto.setUsername(review.getUserId().getUsername());
            dto.setFullName(review.getUserId().getFirstName() + " " + review.getUserId().getLastName());
            dto.setPlaceName(review.getPlaceId().getName());
            dto.setPlaceCity(review.getPlaceId().getCity());
            dto.setPlaceCategory(review.getPlaceId().getCategoryId().getCategoryName());

            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponseDTO updateReview(Long reviewId, ReviewsDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Reviews existingReview = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        if (!existingReview.getUserId().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only edit your own reviews.");
        }

        existingReview.setRating(dto.getRating());
        existingReview.setComment(dto.getComment());
        existingReview.setCreatedAt(LocalDateTime.now());

        Reviews updated = reviewsRepository.save(existingReview);
        updatePlaceRating(updated.getPlaceId());

        return getReviewResponseDTOS(List.of(updated)).get(0);
    }
    public boolean deleteReview(Long reviewId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Reviews existingReview = reviewsRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review with id " + reviewId + " not found"));

        if (!existingReview.getUserId().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not authorized to delete this review");
        }

        reviewsRepository.delete(existingReview);
        return true;
    }
}
