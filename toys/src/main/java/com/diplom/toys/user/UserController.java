package com.diplom.toys.user;

import com.diplom.toys.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class UserController {

    private final OrderRepository orderRepository;

    @GetMapping
    public String profile(Model model) {

        String userId = "test-user";   //<<<<<<< ЗАГЛУШКА

        model.addAttribute("orders", orderRepository.findByUser_Id(userId));

        return "profile";
    }
}