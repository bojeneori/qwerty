package com.diplom.toys.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product getById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));
    }

    public boolean isAvailable(Product product) {
        return product.getQuantityInStock() > 0;
    }

    @Transactional
    public void decreaseStock(Product product, int quantity) {

        if (product.getQuantityInStock() < quantity) {
            throw new RuntimeException("Недостаточно товара на складе");
        }

        product.setQuantityInStock(
                product.getQuantityInStock() - quantity
        );

        productRepository.save(product);
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Transactional
    public void increaseStock(Product product, int quantity) {
        product.setQuantityInStock(
                product.getQuantityInStock() + quantity
        );

        productRepository.save(product);
    }
}