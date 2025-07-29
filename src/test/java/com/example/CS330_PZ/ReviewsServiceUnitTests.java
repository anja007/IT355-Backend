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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewsServiceUnitTests {

    @Test
    public void createReviewsHappyFlow() {
        ReviewsRepository reviewsRepository = mock(ReviewsRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        PlaceRepository placeRepository = mock(PlaceRepository.class);

        ReviewsDTO dto = new ReviewsDTO();
        dto.setPlaceId(1);
        dto.setRating(5);
        dto.setComment("Excellent place!");

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        User mockUser = new User();
        mockUser.setUsername("testuser");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");

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

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
        when(placeRepository.findById(1L)).thenReturn(Optional.of(mockPlace));
        when(reviewsRepository.save(any(Reviews.class))).thenReturn(savedReview);
        when(reviewsRepository.getReviewsByPlaceId(anyInt())).thenReturn(List.of(savedReview));

        ReviewsService reviewsService = new ReviewsService(reviewsRepository, userRepository, placeRepository);

        ReviewResponseDTO result = reviewsService.createReviews(dto);

        assertNotNull(result);
        assertNotNull(result.getReviewId());
        assertNotNull(result.getUsername());
        assertNotNull(result.getPlaceName());

        verify(userRepository).findByUsername("testuser");
        verify(placeRepository).findById(1L);
        verify(reviewsRepository).save(any(Reviews.class));
        verify(placeRepository).save(any(Place.class));
    }

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewsRepository reviewsRepository;

    @InjectMocks
    private ReviewsService reviewsService;

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
}
