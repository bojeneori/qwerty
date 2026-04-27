package com.diplom.toys.auth;

import com.diplom.toys.user.AdminRepository;
import com.diplom.toys.user.User;
import com.diplom.toys.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {

        String email = user.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "Пользователь с таким email уже существует.");
            model.addAttribute("user", user);
            return "register";
        }

        if (adminRepository.existsByEmail(email)) {
            model.addAttribute("error", "Пользователь с таким email уже существует.");
            model.addAttribute("user", user);
            return "register";
        }

        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

        userRepository.save(user);

        return "redirect:/login?registered";
    }
}