package com.diplom.toys.product;

import com.diplom.toys.category.CategoryOption;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(name = "main_image")
    private UUID mainImage;

    @Column(name = "edition_size")
    private Integer editionSize;

    @Column(name = "quantity_in_stock")
    private Integer quantityInStock;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> images;

    @ManyToMany
    @JoinTable(
            name = "product_category_options",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "category_option_id")
    )
    private List<CategoryOption> categoryOptions;

    public String getMainImageUrl() {
        if (images != null && !images.isEmpty()) {
            return images.get(0).getImageUrl();
        }
        return "default.jpg";
    }
}
