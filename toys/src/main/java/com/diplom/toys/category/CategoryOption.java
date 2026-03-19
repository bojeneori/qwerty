package com.diplom.toys.category;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "category_options")
@Data
public class CategoryOption {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String value;
}
