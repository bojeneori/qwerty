package com.diplom.toys.reserv;

import com.diplom.toys.product.Product;
import com.diplom.toys.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Reservation createReservation(UUID productId, UUID userId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getQuantityInStock() <= 0) {
            throw new RuntimeException("Товар закончился");
        }
        System.out.println(product.getQuantityInStock());
        product.setQuantityInStock(
                product.getQuantityInStock() - 1
        );
        System.out.println(product.getQuantityInStock());
        Reservation reservation = Reservation.builder()
                .product(product)
                .userId(userId)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        return reservationRepository.save(reservation);
    }

    @Transactional
    public void removeExpiredReservations() {

        List<Reservation> expired =
                reservationRepository.findByExpiresAtBefore(LocalDateTime.now());

        for (Reservation r : expired) {

            Product product = productRepository.findById(
                    r.getProduct().getId()
            ).orElseThrow();
            System.out.println(product.getQuantityInStock());
            product.setQuantityInStock(product.getQuantityInStock() + 1);
            System.out.println(product.getQuantityInStock());
            productRepository.save(product);
        }

        reservationRepository.deleteAll(expired);
    }

    @Transactional
    public void cancelReservation(UUID reservationId, UUID userId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (!reservation.getUserId().equals(userId)) {
            throw new AccessDeniedException("Access denied");
        }

        Product product = productRepository.findById(
                reservation.getProduct().getId()
        ).orElseThrow(() -> new RuntimeException("Product not found"));

        product.setQuantityInStock(
                product.getQuantityInStock() + 1
        );

        productRepository.save(product);

        reservationRepository.delete(reservation);
    }

}
