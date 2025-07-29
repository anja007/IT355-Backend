package com.example.CS330_PZ.service;

import lombok.Data;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GeocodingService {
    private final RestTemplate restTemplate = new RestTemplate();

    public LatLng geocodeAddress(String address) {
        try {
            // Ručno enkodiranje adrese kao u Node.js
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String url = "https://nominatim.openstreetmap.org/search?format=json&q=" + encodedAddress;

            // Log URL da proveriš ručno
            System.out.println("🛰️ Geocoding URL: " + url);

            // Header za Nominatim (neophodan!)
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "TvojaAplikacija/1.0 (email@example.com)");
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Poziv ka API-ju
            ResponseEntity<NominatimResponse[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    NominatimResponse[].class
            );

            // Provera odgovora
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().length > 0) {
                NominatimResponse result = response.getBody()[0];
                double lat = Double.parseDouble(result.getLat());
                double lon = Double.parseDouble(result.getLon());

                System.out.println("✅ Koordinate uspešno dobijene: lat=" + lat + ", lon=" + lon);
                return new LatLng(lat, lon);
            } else {
                System.err.println("❌ Nema rezultata za adresu: " + address);
            }

        } catch (Exception e) {
            System.err.println("❌ Geocoding greška: " + e.getMessage());
        }

        return null;
    }

    @Data
    public static class NominatimResponse {
        private String lat;
        private String lon;
    }

    @Data
    public static class LatLng {
        private final double lat;
        private final double lng;
    }
}
