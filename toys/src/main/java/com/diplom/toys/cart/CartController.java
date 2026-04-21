package com.diplom.toys.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
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

        List<CartItem> items = cartService.getCartItems(userId);

        // Группируем для отображения
        Map<UUID, Map<String, Object>> groupedItems = new LinkedHashMap<>();

        for (CartItem item : items) {
            UUID productId = item.getProduct().getId();

            if (!groupedItems.containsKey(productId)) {
                Map<String, Object> group = new HashMap<>();
                group.put("product", item.getProduct());
                group.put("quantity", 1);
                groupedItems.put(productId, group);
            } else {
                Map<String, Object> group = groupedItems.get(productId);
                group.put("quantity", (Integer) group.get("quantity") + 1);
            }
        }

        model.addAttribute("groupedItems", groupedItems.values());
        model.addAttribute("total", cartService.getCartTotal(userId));

        return "cart";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable UUID productId) {
        cartService.addToCart(getCurrentUserId(), productId);
        return "redirect:/cart";
    }

    @PostMapping("/increase/{productId}")
    public String increaseQuantity(@PathVariable UUID productId) {
        cartService.increaseQuantity(getCurrentUserId(), productId);
        return "redirect:/cart";
    }

    @PostMapping("/decrease/{productId}")
    public String decreaseQuantity(@PathVariable UUID productId) {
        cartService.decreaseQuantity(getCurrentUserId(), productId);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable UUID productId) {
        cartService.removeAllFromCart(getCurrentUserId(), productId);
        return "redirect:/cart";
    }
}