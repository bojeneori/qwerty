package com.diplom.toys.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public String products(Model model) {

        model.addAttribute("products", productService.getAll());

        return "products";
    }

    @GetMapping("/{id}")
    public String product(@PathVariable String id, Model model) {

        model.addAttribute("product", productService.getById(id));

        return "product";
    }
}