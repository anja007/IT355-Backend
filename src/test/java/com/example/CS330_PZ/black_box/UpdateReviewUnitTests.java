package com.example.CS330_PZ.black_box;

import com.example.CS330_PZ.DTO.ReviewResponseDTO;
import com.example.CS330_PZ.DTO.ReviewsDTO;
import com.example.CS330_PZ.model.Category;
import com.example.CS330_PZ.model.Place;
import com.example.CS330_PZ.model.Reviews;
import com.example.CS330_PZ.model.User;
import com.example.CS330_PZ.repository.PlaceRepository;
import com.example.CS330_PZ.repository.ReviewsRepository;
import com.example.CS330_PZ.repository.UserRepository;
import com.example.CS330_PZ.service.ReviewsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateReviewUnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewsRepository reviewsRepository;

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private ReviewsService reviewsService;

    @Mock
    SecurityContext securityContext;

    @Mock
    Authentication authentication;

    @BeforeEach
    void setUp() {
        reviewsService = new ReviewsService(reviewsRepository, userRepository, placeRepository);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void updateReviewHappyFlow() {
        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        Long reviewId = 1L;
        String username = "testUser";

        User user = new User();
        user.setUserId(1L);
        user.setUsername(username);

        Place place = new Place();
        place.setPlaceId(2L);

        Category category = new Category();
        category.setCategoryId(1L);
        place.setCategoryId(category);

        Reviews review = new Reviews();
        review.setReviewId(reviewId);
        review.setUserId(user);
        review.setPlaceId(place);
        review.setRating(3);
        review.setComment("Old comment");

        ReviewsDTO dto = new ReviewsDTO();
        dto.setRating(4);
        dto.setComment("Updated comment");

        Reviews updatedReview = new Reviews();
        updatedReview.setReviewId(reviewId);
        updatedReview.setUserId(user);
        updatedReview.setPlaceId(place);
        updatedReview.setRating(4);
        updatedReview.setComment("Updated comment");
        updatedReview.setCreatedAt(java.time.LocalDateTime.now());

        given(reviewsRepository.findById(reviewId)).willReturn(Optional.of(review));
        given(reviewsRepository.save(any(Reviews.class))).willReturn(updatedReview);

        ReviewResponseDTO result = reviewsService.updateReview(reviewId, dto);

        assertThat(result).isNotNull();
        assertThat(result.getRating()).isEqualTo(4);
        assertThat(result.getComment()).isEqualTo("Updated comment");

        verify(reviewsRepository).findById(reviewId);
        verify(reviewsRepository).save(any(Reviews.class));
    }

    @Test
    public void updateReview_NotAuthor_throwsAccessDenied() {
        when(authentication.getName()).thenReturn("testUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);

        User other = new User();
        other.setUsername("otherUser");

        Place place = new Place();
        place.setPlaceId(2L);

        Reviews existing = new Reviews();
        existing.setReviewId(10L);
        existing.setUserId(other);
        existing.setPlaceId(place);
        existing.setRating(3);
        existing.setComment("old");

        ReviewsDTO dto = new ReviewsDTO();
        dto.setRating(4);
        dto.setComment("new");

        given(reviewsRepository.findById(10L)).willReturn(Optional.of(existing));

        assertThrows(AccessDeniedException.class,
                () -> reviewsService.updateReview(10L, dto));

        verify(reviewsRepository).findById(10L);
        verify(reviewsRepository, never()).save(any());
    }

    @Test
    public void updateReview_WhenUserNotLoggedIn() {
        when(securityContext.getAuthentication()).thenReturn(null);

        Long reviewId = 1L;
        Reviews existingReview = new Reviews();
        existingReview.setReviewId(reviewId);

        ReviewsDTO updateDTO = new ReviewsDTO();
        updateDTO.setRating(5);
        updateDTO.setComment("Updated comment");

        assertThrows(NullPointerException.class, () -> {
            reviewsService.updateReview(reviewId, updateDTO);
        });

        verify(reviewsRepository, never()).findById(any());
        verify(reviewsRepository, never()).save(any(Reviews.class));
    }

}
