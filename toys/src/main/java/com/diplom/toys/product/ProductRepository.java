package com.diplom.toys.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("""
    SELECT p FROM Product p
    JOIN p.categoryOptions co
    WHERE co.id IN :optionIds
    GROUP BY p.id
    HAVING COUNT(DISTINCT co.id) = :size
""")
    List<Product> findByAllCategoryOptions(
            @Param("optionIds") List<UUID> optionIds,
            @Param("size") long size
    );

    List<Product> findByIsActiveTrue();

    List<Product> findByIsActiveTrueAndQuantityInStockGreaterThan(int stock);

    @Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id = :id")
    Optional<Product> findByIdWithImages(@Param("id") UUID id);

}