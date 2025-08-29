package com.example.CS330_PZ.service;

import com.opencagedata.jopencage.JOpenCageGeocoder;
import com.opencagedata.jopencage.model.JOpenCageForwardRequest;
import com.opencagedata.jopencage.model.JOpenCageResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeocodingService {

    @Value("${opencage.api.key}")
    private String apiKey;

    private JOpenCageGeocoder geocoder;

    @PostConstruct
    public void init() {
        this.geocoder = new JOpenCageGeocoder(apiKey);
    }

    public LatLng geocodeAddress(String fullAddress) {
        JOpenCageForwardRequest forwardRequest = new JOpenCageForwardRequest(fullAddress);
        forwardRequest.setNoAnnotations(true);
        JOpenCageResponse response = geocoder.forward(forwardRequest);

        if (!response.getResults().isEmpty()) {
            double lat = response.getResults().get(0).getGeometry().getLat();
            double lng = response.getResults().get(0).getGeometry().getLng();
            return new LatLng(lat, lng);
        }

        throw new RuntimeException("Geocoding failed: no results for address: " + fullAddress);
    }

    public static class LatLng {
        double lat;
        double lng;

        public LatLng(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }

}
