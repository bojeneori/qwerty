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

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationService reservationService;


    public Cart getOrCreateCart(String userId) {

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
    public void addToCart(String userId, String productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        // проверка доступности
        if (!reservationService.isProductAvailable(productId, product.getQuantityInStock())) {
            throw new RuntimeException("Товар уже разобран");
        }

        Cart cart = getOrCreateCart(userId);

        // нельзя добавить два раза
        cartItemRepository.findByCart_IdAndProduct_Id(cart.getId(), productId)
                .ifPresent(item -> {
                    throw new RuntimeException("Товар уже в корзине");
                });

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
    public void removeFromCart(String userId, String productId) {

        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository
                .findByCart_IdAndProduct_Id(cart.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден в корзине"));

        // удалить бронь
        if (item.getReservationId() != null) {
            reservationRepository.deleteById(item.getReservationId());
        }

        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);

        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());

        for (CartItem item : items) {
            if (item.getReservationId() != null) {
                reservationRepository.deleteById(item.getReservationId());
            }
        }

        cartItemRepository.deleteAll(items);
    }

    public double getCartTotal(String userId) {
        Cart cart = getOrCreateCart(userId);

        List<CartItem> items = cartItemRepository.findByCart_Id(cart.getId());

        return items.stream()
                .map(CartItem::getProduct)
                .mapToDouble(Product::getPrice)
                .sum();
    }

    public List<CartItem> getCartItems(String userId) {
        Cart cart = getOrCreateCart(userId);
        return cartItemRepository.findByCart_Id(cart.getId());
    }
}
