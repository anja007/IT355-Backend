package com.example.CS330_PZ.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@Table(name = "places")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id")
    private Long placeId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category categoryId;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "rating")
    private Double rating;

    @ElementCollection
    @CollectionTable(name = "place_tags")
    private List<String> tags = new ArrayList<>();

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lng")
    private Double lng;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        return placeId != null && placeId.equals(place.placeId);
    }

    @Override
    public int hashCode() {
        return placeId != null ? placeId.hashCode() : 0;
    }
}
