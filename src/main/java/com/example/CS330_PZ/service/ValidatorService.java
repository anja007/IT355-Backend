package com.example.CS330_PZ.service;

import org.springframework.stereotype.Service;

@Service
public class ValidatorService {
    public boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,3}$";
        return email != null && email.matches(emailRegex);
    }

    public void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
}
