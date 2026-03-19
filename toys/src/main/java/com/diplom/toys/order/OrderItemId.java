package com.diplom.toys.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderItemId implements Serializable {
    private String order;
    private String product;
}
