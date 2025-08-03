package com.example.CS330_PZ.repository;

import com.example.CS330_PZ.model.Category;
import com.example.CS330_PZ.model.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findById(Long id);

    Page<Place> findAll(Pageable pageable);

    List<Place> findByCategoryId(Category category);


   @Query("SELECT DISTINCT p FROM Place p JOIN p.tags t WHERE " +
            "LOWER(t) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.city) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.address) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Place> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    List<Place> findTop5ByOrderByRatingDesc();

    /*

    Page<Place> findByNameContainingIgnoreCaseOrCityContainingIgnoreCaseOrAddressContainingIgnoreCase(String name, String city, String address, Pageable pageable);
*/

}
