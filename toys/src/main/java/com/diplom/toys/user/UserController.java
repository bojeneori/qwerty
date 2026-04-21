package com.diplom.toys.user;

import com.diplom.toys.cart.Cart;
import com.diplom.toys.cart.CartRepository;
import com.diplom.toys.order.Order;
import com.diplom.toys.order.OrderRepository;
import com.diplom.toys.reserv.Reservation;
import com.diplom.toys.reserv.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class UserController {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    private UUID getCurrentUserId() {
        return UUID.fromString(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName());
    }

    @GetMapping
    public String profile(Model model) {
        UUID userId = getCurrentUserId();

        // Получаем данные пользователя
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Получаем заказы пользователя
        List<Order> orders = orderRepository.findByUser_Id(userId);

        // Получаем корзину пользователя
        Cart cart = cartRepository.findByUser_Id(userId).orElse(null);

        // Получаем активные бронирования пользователя
        List<Reservation> activeReservations = reservationRepository
                .findByUserIdAndExpiresAtAfter(userId, LocalDateTime.now());

        // Добавляем все в модель
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("cart", cart);
        model.addAttribute("reservations", activeReservations);

        return "profile";
    }
}