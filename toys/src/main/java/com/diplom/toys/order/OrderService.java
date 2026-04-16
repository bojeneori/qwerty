package com.diplom.toys.order;

import com.diplom.toys.cart.Cart;
import com.diplom.toys.cart.CartItem;
import com.diplom.toys.cart.CartItemRepository;
import com.diplom.toys.cart.CartService;
import com.diplom.toys.product.Product;
import com.diplom.toys.product.ProductRepository;
import com.diplom.toys.reserv.ReservationRepository;
import com.diplom.toys.user.User;
import com.diplom.toys.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    @Transactional
    public Order createOrder(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Cart cart = cartService.getOrCreateCart(userId);

        List<CartItem> cartItems = cartItemRepository.findByCart_Id(cart.getId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Корзина пуста");
        }

        double totalPrice = cartItems.stream()
                .map(item -> item.getProduct().getPrice())
                .mapToDouble(Double::doubleValue)
                .sum();

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setPrice(totalPrice);

        order = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            if (product.getQuantityInStock() <= 0) {
                throw new RuntimeException("Товар закончился: " + product.getName());
            }

            product.setQuantityInStock(product.getQuantityInStock() - 1);
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);

            orderItemRepository.save(orderItem);

            if (cartItem.getReservationId() != null) {
                reservationRepository.deleteById(cartItem.getReservationId());
            }
        }

        cartItemRepository.deleteAll(cartItems);

        return order;
    }
}
