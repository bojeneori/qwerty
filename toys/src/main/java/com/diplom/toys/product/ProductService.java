package com.diplom.toys.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product getById(UUID id) {
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

    public List<Product> findProducts(String search, List<UUID> filters) {

        List<Product> products;

        if (filters != null && !filters.isEmpty()) {
            products = productRepository.findByAllCategoryOptions(filters, filters.size());
        } else {
            products = productRepository.findAll();
        }

        if (search != null && !search.isBlank()) {
            String lower = search.toLowerCase();

            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(lower))
                    .toList();
        }

        return products;
    }
}