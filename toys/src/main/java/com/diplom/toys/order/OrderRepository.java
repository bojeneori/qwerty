package com.diplom.toys.order;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByUser_IdOrderByOrderDateDesc(UUID userId);

    List<Order> findByUser_Id(UUID userId);
}