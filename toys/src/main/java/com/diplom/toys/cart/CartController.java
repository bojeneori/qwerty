package com.diplom.toys.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    private String getCurrentUserId() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
    }

    @GetMapping
    public String cart(Model model) {

        String userId = getCurrentUserId();

        model.addAttribute("items", cartService.getCartItems(userId));
        model.addAttribute("total", cartService.getCartTotal(userId));

        return "cart";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable String productId) {

        cartService.addToCart(getCurrentUserId(), productId);

        return "redirect:/cart";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable String productId) {

        cartService.removeFromCart(getCurrentUserId(), productId);

        return "redirect:/cart";
    }
}