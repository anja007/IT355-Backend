package com.example.CS330_PZ;

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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewsServiceUnitTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewsRepository reviewsRepository;

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private ReviewsService reviewsService;


    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void createReviewsHappyFlow() {
        Authentication auth = mock(Authentication.class);
        given(auth.getName()).willReturn("testUser");

        SecurityContext context = mock(SecurityContext.class);
        given(context.getAuthentication()).willReturn(auth);

        SecurityContextHolder.setContext(context);

        ReviewsDTO dto = new ReviewsDTO();
        dto.setPlaceId(1);
        dto.setRating(5);
        dto.setComment("Excellent place!");

        User mockUser = new User();
        mockUser.setUsername("testUser");

        Category category = new Category();
        category.setCategoryName("Cafe");

        Place mockPlace = new Place();
        mockPlace.setPlaceId(1L);
        mockPlace.setName("Test Place");
        mockPlace.setCity("Test City");
        mockPlace.setCategoryId(category);

        Reviews savedReview = new Reviews();
        savedReview.setReviewId(1L);
        savedReview.setRating(5);
        savedReview.setComment("Excellent place!");
        savedReview.setUserId(mockUser);
        savedReview.setPlaceId(mockPlace);

        given(userRepository.findByUsername("testUser")).willReturn(Optional.of(mockUser));
        given(placeRepository.findById(1L)).willReturn(Optional.of(mockPlace));
        given(reviewsRepository.save(any(Reviews.class))).willReturn(savedReview);
        given(reviewsRepository.getReviewsByPlaceId(anyInt())).willReturn(List.of(savedReview));

        ReviewResponseDTO result = reviewsService.createReviews(dto);

        assertNotNull(result);
        assertNotNull(result.getReviewId());
        assertNotNull(result.getUsername());
        assertNotNull(result.getPlaceName());

        verify(userRepository).findByUsername("testUser");
        verify(placeRepository).findById(1L);
        verify(reviewsRepository).save(any(Reviews.class));
        verify(placeRepository).save(any(Place.class));
    }

    @Test
    public void getReviewsForUserHappyFlow() {
        String username = "user1";
        User user = new User();
        user.setUserId(1L);
        user.setUsername(username);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));

        Reviews review1 = new Reviews();
        Reviews review2 = new Reviews();
        List<Reviews> reviews = Arrays.asList(review1, review2);

        given(reviewsRepository.getReviewsByUserId(1)).willReturn(reviews);

        List<Reviews> result = reviewsService.getReviewsForUser(username);

        assertEquals(2, result.size());

        verify(userRepository).findByUsername(username);
        verify(reviewsRepository).getReviewsByUserId(Math.toIntExact(user.getUserId()));
    }

    @Test
    public void updateReviewHappyFlow() {
        Authentication auth = mock(Authentication.class);
        given(auth.getName()).willReturn("testUser");

        SecurityContext context = mock(SecurityContext.class);
        given(context.getAuthentication()).willReturn(auth);

        SecurityContextHolder.setContext(context);

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
    public void deleteReviewHappyFlow() {
        Authentication auth = mock(Authentication.class);
        given(auth.getName()).willReturn("testUser");

        SecurityContext context = mock(SecurityContext.class);
        given(context.getAuthentication()).willReturn(auth);

        SecurityContextHolder.setContext(context);

        Long reviewId = 1L;
        String username = "testUser";

        User user = new User();
        user.setUserId(1L);
        user.setUsername(username);

        Reviews existingReview = new Reviews();
        existingReview.setReviewId(reviewId);
        existingReview.setUserId(user);
        existingReview.setComment("comment");
        existingReview.setRating(4);

        given(reviewsRepository.findById(reviewId)).willReturn(Optional.of(existingReview));

        boolean result = reviewsService.deleteReview(reviewId);

        assertThat(result).isTrue();
        verify(reviewsRepository).findById(reviewId);
        verify(reviewsRepository).delete(existingReview);
    }

    @Test
    public void deleteReviewNotThatUser() {
        Long reviewId = 2L;
        String loggedUser = "testUser";
        String otherUser = "otherUser";

        Authentication auth = mock(Authentication.class);
        given(auth.getName()).willReturn(loggedUser);

        SecurityContext context = mock(SecurityContext.class);
        given(context.getAuthentication()).willReturn(auth);
        SecurityContextHolder.setContext(context);

        User other = new User();
        other.setUsername(otherUser);

        Reviews review = new Reviews();
        review.setReviewId(reviewId);
        review.setUserId(other);

        given(reviewsRepository.findById(reviewId)).willReturn(Optional.of(review));

        boolean result = reviewsService.deleteReview(reviewId);

        assertThat(result).isFalse();
        verify(reviewsRepository, never()).delete(any());
    }
}
