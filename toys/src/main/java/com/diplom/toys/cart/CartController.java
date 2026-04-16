package com.diplom.toys.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    private UUID getCurrentUserId() {
        return UUID.fromString(SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName());
    }

    @GetMapping
    public String cart(Model model) {

        UUID userId = getCurrentUserId();

        model.addAttribute("items", cartService.getCartItems(userId));
        model.addAttribute("total", cartService.getCartTotal(userId));

        return "cart";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable UUID productId) {

        cartService.addToCart(getCurrentUserId(), productId);

        return "redirect:/cart";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable UUID productId) {

        cartService.removeFromCart(getCurrentUserId(), productId);

        return "redirect:/cart";
    }
}