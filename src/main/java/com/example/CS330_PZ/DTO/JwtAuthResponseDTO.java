package com.example.CS330_PZ.DTO;

import lombok.Data;

@Data
public class JwtAuthResponseDTO {
    private String accessToken;
    private String tokenType="Bearer";
}
