package com.diplom.toys.reserv;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    List<Reservation> findByProductIdAndExpiresAtAfter(UUID productId, LocalDateTime now);

    List<Reservation> findByExpiresAtBefore(LocalDateTime now);
}