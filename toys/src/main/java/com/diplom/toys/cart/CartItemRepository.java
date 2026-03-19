package com.diplom.toys.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, String> {

    List<CartItem> findByCart_Id(String cartId);

    Optional<CartItem> findByCart_IdAndProduct_Id(String cartId, String productId);
}