package com.diplom.toys.order;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    public boolean pay(UUID orderId, double amount) {

        return true;
    }
}
