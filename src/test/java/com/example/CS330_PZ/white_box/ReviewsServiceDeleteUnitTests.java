package com.example.CS330_PZ.white_box;

import com.example.CS330_PZ.model.Reviews;
import com.example.CS330_PZ.model.User;
import com.example.CS330_PZ.repository.ReviewsRepository;
import com.example.CS330_PZ.service.ReviewsService;
import jakarta.persistence.EntityNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewsServiceDeleteUnitTests {

    @Mock
    private ReviewsRepository reviewsRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private ReviewsService reviewsService;

    private User testUser;
    private User otherUser;
    private Reviews userReview;

    @BeforeEach
    void setUp() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");

        otherUser = new User();
        otherUser.setUserId(2L);
        otherUser.setUsername("otheruser");

        userReview = new Reviews();
        userReview.setReviewId(20L);
        userReview.setUserId(testUser);
    }

    @Test
    void testStatementCoverage_deleteReview_HappyPath() {
        when(reviewsRepository.findById(20L))
                .thenReturn(Optional.of(userReview));
        when(authentication.getName()).thenReturn("testuser");
        doNothing().when(reviewsRepository).delete(userReview);

        boolean result = reviewsService.deleteReview(20L);

        assertTrue(result);
        verify(reviewsRepository).findById(20L);
        verify(reviewsRepository).delete(userReview);
    }

    @Test
    void testBranchCoverage_deleteReview_NotAuthor() {
        when(reviewsRepository.findById(20L))
                .thenReturn(Optional.of(userReview));
        when(authentication.getName()).thenReturn("otheruser");

        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> reviewsService.deleteReview(20L)
        );
        assertEquals("You are not authorized to delete this review",
                ex.getMessage());

        verify(reviewsRepository).findById(20L);
        verify(reviewsRepository, never()).delete(any());
    }

    @Test
    void testBranchCoverage_deleteReview_ReviewNotFound() {
       when(reviewsRepository.findById(100L))
                .thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("testuser");

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> reviewsService.deleteReview(100L)
        );
        assertEquals("Review with id 100 not found", ex.getMessage());

        verify(reviewsRepository).findById(100L);
        verify(reviewsRepository, never()).delete(any());
    }

    @Test
    void testBranchCoverage_deleteReview_NotLoggedIn() {
        when(reviewsRepository.findById(20L))
                .thenReturn(Optional.of(userReview));
        when(authentication.getName()).thenReturn("anonymousUser");

        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> reviewsService.deleteReview(20L)
        );
        assertEquals("You are not authorized to delete this review",
                ex.getMessage());

        verify(reviewsRepository).findById(20L);
        verify(reviewsRepository, never()).delete(any());
    }

    @Test
    void testPathCoverage_deleteReview_AllPaths() {
        when(reviewsRepository.findById(20L))
                .thenReturn(Optional.of(userReview));
        when(authentication.getName()).thenReturn("testuser");
        doNothing().when(reviewsRepository).delete(userReview);
        assertTrue(reviewsService.deleteReview(20L));


        when(authentication.getName()).thenReturn("otheruser");
        assertThrows(AccessDeniedException.class,
                () -> reviewsService.deleteReview(20L));


        when(reviewsRepository.findById(100L))
                .thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("testuser");
        assertThrows(EntityNotFoundException.class,
                () -> reviewsService.deleteReview(100L));
    }
}