package com.diplom.toys.cart;


import com.diplom.toys.product.Product;
import com.diplom.toys.product.ProductRepository;
import com.diplom.toys.reserv.Reservation;
import com.diplom.toys.reserv.ReservationRepository;
import com.diplom.toys.reserv.ReservationService;
import com.diplom.toys.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;

    public Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUser_Id(userId)
                .orElseGet(() -> {
                    User user = new User();
                    user.setId(userId);

                    Cart cart = Cart.builder()
                            .user(user)
                            .expiresAt(LocalDateTime.now().plusHours(24))
                            .build();

                    return cartRepository.save(cart);
                });
    }

    @Transactional
    public void addToCart(UUID userId, UUID productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        Cart cart = getOrCreateCart(userId);

        // считаем сколько уже таких товаров в корзине
        List<CartItem> existingItems = cartItemRepository.findByCart_Id(cart.getId()).stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .toList();

        long currentQuantity = existingItems.size();

        // проверяем доступность
        if (!reservationService.isProductAvailable(productId, (int) (product.getQuantityInStock() - currentQuantity))) {
            throw new RuntimeException("Недостаточно товара на складе");
        }

        Reservation reservation = reservationService.createReservation(productId, userId);

        CartItem item = CartItem.builder()
                .cart(cart)
                .product(product)
                .reservationId(reservation.getId())
                .build();

        cartItemRepository.save(item);

        cart.setExpiresAt(LocalDateTime.now().plusHours(24));
        cartRepository.save(cart);
    }

    @Transactional
    public void decreaseQuantity(UUID userId, UUID productId) {
        Cart cart = getOrCreateCart(userId);

        // находим ВСЕ записи этого товара
        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId()).stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .toList();

        if (items.isEmpty()) {
            throw new RuntimeException("Товар не найден в корзине");
        }

        CartItem itemToRemove = items.get(0);

        // удаляем бронь
        if (itemToRemove.getReservationId() != null) {
            reservationRepository.deleteById(itemToRemove.getReservationId());
        }

        cartItemRepository.delete(itemToRemove);
    }

    @Transactional
    public void removeAllFromCart(UUID userId, UUID productId) {
        Cart cart = getOrCreateCart(userId);

        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId()).stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .toList();

        for (CartItem item : items) {
            if (item.getReservationId() != null) {
                reservationRepository.deleteById(item.getReservationId());
            }
            cartItemRepository.delete(item);
        }
    }

    @Transactional
    public void removeFromCart(UUID userId, UUID productId) {
        decreaseQuantity(userId, productId);
    }

    @Transactional
    public void clearCart(UUID userId) {
        Cart cart = getOrCreateCart(userId);

        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());

        for (CartItem item : items) {
            if (item.getReservationId() != null) {
                reservationRepository.deleteById(item.getReservationId());
            }
        }

        cartItemRepository.deleteAll(items);
    }

    public double getCartTotal(UUID userId) {
        Cart cart = getOrCreateCart(userId);

        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());

        return items.stream()
                .map(CartItem::getProduct)
                .mapToDouble(Product::getPrice)
                .sum();
    }

    public List<CartItem> getCartItems(UUID userId) {
        Cart cart = getOrCreateCart(userId);
        return cartItemRepository.findByCart_Id(cart.getId());
    }

    @Transactional
    public void increaseQuantity(UUID userId, UUID productId) {
        addToCart(userId, productId);
    }
}