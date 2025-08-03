package com.example.CS330_PZ.DTO;

import lombok.Data;

@Data
public class ReviewResponseDTO {
    private int reviewId;
    private String comment;
    private int rating;
    private String createdAt;

    private String username;
    private String fullName;

    private String placeName;
    private String placeCity;
    private String placeCategory;
}
