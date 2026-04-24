package com.diplom.toys.product;

import com.diplom.toys.category.CategoryService;
import com.diplom.toys.reserv.Reservation;
import com.diplom.toys.reserv.ReservationService;
import com.diplom.toys.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/product/{id}")
    public String product(@PathVariable UUID id, Model model) {
        Product product = productService.getById(id);
        model.addAttribute("product", product);

        if (product.getImages() != null) {
            product.getImages().size();
        }

        return "product";
    }

}