package com.diplom.toys.cart;

import lombok.Data;

import java.io.Serializable;

@Data
public class CartItemId implements Serializable {
    private String cart;
    private String product;
}