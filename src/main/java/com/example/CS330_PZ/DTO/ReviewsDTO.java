package com.example.CS330_PZ.DTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewsDTO {
    private int placeId;
    private int rating;
    private String comment;
}

