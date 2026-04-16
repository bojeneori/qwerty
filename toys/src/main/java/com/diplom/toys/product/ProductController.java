package com.diplom.toys.product;

import com.diplom.toys.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<UUID> filters,
            Model model) {

        model.addAttribute("products", productService.findProducts(search, filters));
        model.addAttribute("categories", categoryService.getAllWithOptions());

        return "products";
    }

    @GetMapping("/{id}")
    public String product(@PathVariable UUID id, Model model) {

        model.addAttribute("product", productService.getById(id));

        return "product";
    }
}