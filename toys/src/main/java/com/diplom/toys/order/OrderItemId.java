package com.diplom.toys.order;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class OrderItemId implements Serializable {
    private UUID order;
    private UUID product;
}
