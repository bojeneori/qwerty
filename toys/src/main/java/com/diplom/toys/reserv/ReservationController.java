package com.diplom.toys.reserv;

import com.diplom.toys.user.UserRepository;
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
@RequestMapping("/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;

    private final UserRepository userRepository;
    private UUID getCurrentUserId() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @PostMapping("/{id}")
    public String reserve(@PathVariable UUID id) {

        UUID userId = getCurrentUserId();

        Reservation reservation = reservationService.createReservation(id, userId);

        return "redirect:/reservation/confirm/" + reservation.getId();
    }

    @GetMapping("/confirm/{id}")
    public String confirm(@PathVariable UUID id, Model model) {

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        model.addAttribute("reservation", reservation);
        model.addAttribute("product", reservation.getProduct());

        return "reservation";
    }
}
