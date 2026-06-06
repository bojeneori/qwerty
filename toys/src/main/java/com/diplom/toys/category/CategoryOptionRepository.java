package com.diplom.toys.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CategoryOptionRepository extends JpaRepository<CategoryOption, UUID> {
}