package com.diplom.toys.reserv;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

    List<Reservation> findByProductIdAndExpiresAtAfter(String productId, LocalDateTime now);

    List<Reservation> findByExpiresAtBefore(LocalDateTime now);
}