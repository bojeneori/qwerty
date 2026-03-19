package com.diplom.toys.order;

import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByUser_IdOrderByOrderDateDesc(String userId);

    List<Order> findByUser_Id(String userId);
}