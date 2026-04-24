package com.diplom.toys.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    List<CartItem> findByCart_Id(UUID cartId);
    void deleteByReservationId(UUID reservationId);
    Optional<CartItem> findByCart_IdAndProduct_Id(UUID cartId, UUID productId);
}