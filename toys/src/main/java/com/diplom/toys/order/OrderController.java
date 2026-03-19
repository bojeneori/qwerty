package com.diplom.toys.order;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    private String getCurrentUserId() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    @PostMapping("/checkout")
    public String checkout() {

        orderService.createOrder(getCurrentUserId());

        return "redirect:/orders";
    }

    @GetMapping
    public String orders(Model model) {

        model.addAttribute(
                "orders",
                orderRepository.findByUser_IdOrderByOrderDateDesc(getCurrentUserId())
        );

        return "orders";
    }
}