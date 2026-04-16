package com.diplom.toys.user;

import com.diplom.toys.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class UserController {

    private final OrderRepository orderRepository;

    private UUID getCurrentUserId() {
        return UUID.fromString(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName());
    }

    @GetMapping
    public String profile(Model model) {

        UUID userId = getCurrentUserId(); // Извлекаем UUID пользователя из контекста

        model.addAttribute("orders", orderRepository.findByUser_Id(userId));

        return "profile";
    }


}