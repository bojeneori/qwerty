package com.diplom.toys.product;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findDistinctByCategoryOptionsCategoryId(String categoryId);

    List<Product> findByIsActiveTrue();

    List<Product> findByIsActiveTrueAndQuantityInStockGreaterThan(int stock);
}