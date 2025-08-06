package com.example.CS330_PZ.DTO;

import lombok.Data;
import java.util.List;

@Data
public class PlaceDTO {
    private String name;
    private String address;
    private String city;
    private Double lat;
    private Double lng;
    private int categoryId;
    private Double rating;
    private List<String> tags;
    private List<String> photos;


}
