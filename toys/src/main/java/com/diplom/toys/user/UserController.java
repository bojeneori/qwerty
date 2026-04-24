package com.diplom.toys.user;

import com.diplom.toys.cart.Cart;
import com.diplom.toys.cart.CartItem;
import com.diplom.toys.cart.CartItemRepository;
import com.diplom.toys.cart.CartRepository;
import com.diplom.toys.order.Order;
import com.diplom.toys.order.OrderRepository;
import com.diplom.toys.reserv.Reservation;
import com.diplom.toys.reserv.ReservationRepository;
import com.diplom.toys.reserv.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    private final CartItemRepository cartItemRepository;
    private final PasswordEncoder passwordEncoder;

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

    @PostMapping("/reservations/{reservationId}/cancel")
    @Transactional
    public String cancelReservation(@PathVariable UUID reservationId) {
        UUID userId = getCurrentUserId();

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Бронирование не найдено"));

        if (!reservation.getUserId().equals(userId)) {
            throw new AccessDeniedException("Доступ запрещён");
        }

        // Удаляем все позиции корзины, связанные с этим бронированием
        cartItemRepository.deleteByReservationId(reservationId);

        // Затем удаляем бронирование
        reservationRepository.delete(reservation);

        return "redirect:/profile";
    }
    @GetMapping("/edit")
    public String editProfile(Model model) {
        UUID userId = getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        return "edit-profile";
    }

    @PostMapping("/edit")
    @Transactional
    public String updateProfile(
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam(required = false) String defaultAddress,
            @RequestParam(required = false) String defaultCity,
            @RequestParam(required = false) String defaultPostalCode,
            @RequestParam String currentPassword,
            Model model
    ) {

        UUID userId = getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            model.addAttribute("error", "Неверный пароль");
            model.addAttribute("user", user);
            return "edit-profile";
        }

        if (!user.getEmail().equals(email)) {
            if (userRepository.existsByEmail(email)) {
                model.addAttribute("error", "Email уже используется другим пользователем");
                model.addAttribute("user", user);
                return "edit-profile";
            }
            user.setEmail(email);
        }

        user.setFullName(fullName);
        user.setDefaultAddress(defaultAddress);
        user.setDefaultCity(defaultCity);
        user.setDefaultPostalCode(defaultPostalCode);

        userRepository.save(user);

        return "redirect:/profile";
    }

}