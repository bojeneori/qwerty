package com.diplom.toys.category;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "categories")
@Data
public class Category {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<CategoryOption> options;
}