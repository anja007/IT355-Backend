package com.example.CS330_PZ.repository;

import com.example.CS330_PZ.model.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewsRepository extends JpaRepository<Reviews, Long> {
    @Query("SELECT r from Reviews r")
    List<Reviews> getAllReviews();

    @Query("SELECT r FROM Reviews r WHERE r.placeId.placeId = :placeId")
    List<Reviews> getReviewsByPlaceId(@Param("placeId") Integer placeId);

    @Query("SELECT r FROM Reviews r WHERE r.userId.userId = :userId")
    List<Reviews> getReviewsByUserId(@Param("userId") Integer userId);
}
