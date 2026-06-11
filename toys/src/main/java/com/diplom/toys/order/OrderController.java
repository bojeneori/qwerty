package com.diplom.toys.order;

import com.diplom.toys.cart.CartItem;
import com.diplom.toys.cart.CartService;
import com.diplom.toys.product.Product;
import com.diplom.toys.user.User;
import com.diplom.toys.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final CartService cartService;

    private UUID getCurrentUserId() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model) {

        UUID userId = getCurrentUserId();

        List<CartItem> items = cartService.getCartItems(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double total = items.stream()
                .mapToDouble(i -> i.getProduct().getPrice())
                .sum();

        model.addAttribute("items", items);
        model.addAttribute("total", total);
        model.addAttribute("user", user);

        return "checkout";
    }

    @PostMapping("/checkout")
    public String checkout(RedirectAttributes redirectAttributes) {

        Order order = orderService.createOrder(getCurrentUserId());

        redirectAttributes.addFlashAttribute("orderId", order.getId());

        return "redirect:/orders/pay/" + order.getId();
    }

    @GetMapping("/pay/{orderId}")
    public String payPage(@PathVariable UUID orderId,
                          Model model) {

        model.addAttribute("orderId", orderId);

        return "orders/payment";
    }

    @PostMapping("/pay/{orderId}")
    public String pay(@PathVariable UUID orderId,
                      RedirectAttributes redirectAttributes) {

        orderService.payForOrder(orderId);

        redirectAttributes.addFlashAttribute("success", "Оплата прошла успешно");

        return "redirect:/orders/success";
    }

    @GetMapping("/success")
    public String successPage() {
        return "order-success";
    }
}