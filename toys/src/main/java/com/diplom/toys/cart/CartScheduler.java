package com.diplom.toys.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartScheduler {

    private final CartService cartService;

    @Scheduled(fixedRate = 60000)
    public void cleanExpiredCarts() {
        cartService.removeExpiredCarts();
    }

}
