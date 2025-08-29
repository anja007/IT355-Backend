package com.example.CS330_PZ.integration_tests;

import com.example.CS330_PZ.model.Category;
import com.example.CS330_PZ.model.Place;
import com.example.CS330_PZ.model.Reviews;
import com.example.CS330_PZ.model.User;
import com.example.CS330_PZ.repository.CategoryRepository;
import com.example.CS330_PZ.repository.PlaceRepository;
import com.example.CS330_PZ.repository.ReviewsRepository;
import com.example.CS330_PZ.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
class ReviewWorkflowIntegrationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private PlaceRepository placeRepository;
    @Autowired private ReviewsRepository reviewsRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private long placeId;
    private static final String REVIEW_CREATE_ENDPOINT = "/api/reviews/createReview";

    @BeforeEach
    void setUp() {
        User u = new User();
        u.setUsername("anja123");
        u.setPassword(passwordEncoder.encode("password"));
        u.setEmail("anja@example.com");
        u.setFirstName("Anja");
        u.setLastName("Tester");
        u.setRole(com.example.CS330_PZ.enums.Role.USER);
        userRepository.save(u);

        Category cat = new Category();
        cat.setCategoryName("TestCat");
        categoryRepository.save(cat);

        Place p = new Place();
        p.setName("TestPlace");
        p.setAddress("Test Address");
        p.setCity("TestCity");
        p.setCategoryId(cat);
        p.setTags(new java.util.ArrayList<>(java.util.List.of("tag1")));
        p.setRating(0.0);
        p.setLat(0.0);
        p.setLng(0.0);
        p.setPhotos(null);
        placeId = placeRepository.save(p).getPlaceId();
    }

    @Test
    void fullWorkflow_Login_SelectPlace_CreateReview() throws Exception {
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                 {"username":"anja123","password":"password"}
                                 """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String token = extractJwt(loginResponse);
        assertThat(token).isNotBlank();


        mockMvc.perform(get("/api/places/{id}", placeId)
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.placeId").value((int) placeId))
                .andExpect(jsonPath("$.name").value("TestPlace"));

        String reviewJson = """
                {
                  "placeId": %d,
                  "rating": 5,
                  "comment": "everything was great"
                }
                """.formatted(placeId);

        mockMvc.perform(post(REVIEW_CREATE_ENDPOINT)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reviewId").exists())
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.comment").value("everything was great"))
                .andExpect(jsonPath("$.createdAt").exists());

        List<Reviews> all = reviewsRepository.getReviewsByPlaceId((int) placeId);
        assertThat(all)
                .hasSize(1)
                .first()
                .extracting(Reviews::getComment, r -> r.getUserId().getUsername())
                .containsExactly("everything was great", "anja123");
    }

    private String extractJwt(String loginResponse) {
        try {
            JsonNode node = objectMapper.readTree(loginResponse);
            if (node.hasNonNull("accessToken")) return node.get("accessToken").asText();
            if (node.hasNonNull("token")) return node.get("token").asText();
        } catch (Exception ignored) {}
        throw new IllegalStateException("JWT not found: " + loginResponse);
    }
}
