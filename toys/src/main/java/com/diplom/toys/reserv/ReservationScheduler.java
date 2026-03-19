package com.diplom.toys.reserv;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationScheduler {

    private final ReservationService reservationService;

    @Scheduled(fixedRate = 60000)
    public void cleanExpired() {
        reservationService.removeExpiredReservations();
    }
}