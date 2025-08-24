package com.example.CS330_PZ.black_box;

import com.example.CS330_PZ.service.ValidatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RatingValidatorServiceIntegrationTests {

    @Autowired
    private ValidatorService validatorService;

    @Test
    void testMinBoundary_Legal() {
        assertDoesNotThrow(() -> validatorService.validateRating(1));
    }

    @Test
    void testJustAboveMin_Legal() {
        assertDoesNotThrow(() -> validatorService.validateRating(2));
    }

    @Test
    void testJustBelowMax_Legal() {
        assertDoesNotThrow(() -> validatorService.validateRating(4));
    }

    @Test
    void testMaxBoundary_Legal() {
        assertDoesNotThrow(() -> validatorService.validateRating(5));
    }

    @Test
    void testBelowMin_Illegal() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validatorService.validateRating(-1)
        );
        assertEquals("Rating must be between 1 and 5", ex.getMessage());
    }

    @Test
    void testAboveMax_Illegal() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validatorService.validateRating(6)
        );
        assertEquals("Rating must be between 1 and 5", ex.getMessage());
    }
}
