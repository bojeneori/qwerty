package com.diplom.toys.reserv;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public boolean isProductAvailable(UUID productId, int stock) {
        List<Reservation> activeReservations =
                reservationRepository.findByProductIdAndExpiresAtAfter(
                        productId, LocalDateTime.now()
                );

        return activeReservations.size() < stock;
    }

    public Reservation createReservation(UUID productId, UUID userId) {
        Reservation reservation = Reservation.builder()
                .productId(productId)
                .userId(userId)
                .expiresAt(LocalDateTime.now().plusDays(1)) // <<<<<<<<<<<<<<< ВРЕМЯ
                .build();

        return reservationRepository.save(reservation);
    }

    public void removeExpiredReservations() {
        List<Reservation> expired =
                reservationRepository.findByExpiresAtBefore(LocalDateTime.now());

        reservationRepository.deleteAll(expired);
    }

}
