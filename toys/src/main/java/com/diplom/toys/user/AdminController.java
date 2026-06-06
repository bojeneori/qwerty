package com.diplom.toys.user;

import com.diplom.toys.category.Category;
import com.diplom.toys.category.CategoryOptionRepository;
import com.diplom.toys.category.CategoryService;
import com.diplom.toys.product.Product;
import com.diplom.toys.product.ProductRepository;
import com.diplom.toys.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductService productService;
    private final CategoryOptionRepository categoryOptionRepository;
    private final CategoryService categoryService;

    @GetMapping
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/products")
    public String products(Model model) {
        List<Product> allProducts = productService.findAllProducts();
        model.addAttribute("products", allProducts);
        return "admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductPage(@PathVariable UUID id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        model.addAttribute(
                "categoryOptions",
                categoryOptionRepository.findAll()
        );
        return "admin/product-edit";
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(
            @PathVariable UUID id,
            @ModelAttribute Product product,
            @RequestParam(required = false) List<UUID> categoryOptionIds,
            @RequestParam(required = false) List<String> imageUrls,
            @RequestParam(required = false) MultipartFile[] images,
            @RequestParam(required = false) Integer mainImageIndex
    ) throws IOException {

        productService.updateProduct(
                id,
                product,
                categoryOptionIds,
                imageUrls,
                images,
                mainImageIndex
        );
        return "redirect:/admin/products";
    }

    @PostMapping("/products/toggle-active/{id}")
    public String toggleProductActive(@PathVariable UUID id) {
        productService.toggleActiveStatus(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        List<Category> allCategories = categoryService.findAll();
        model.addAttribute("categories", allCategories);
        return "admin/categories";
    }

    @GetMapping("/categories/create")
    public String createCategoryPage(Model model) {
        model.addAttribute("category", new Category());
        return "admin/category-create";
    }

    @PostMapping("/categories/create")
    public String createCategory(@ModelAttribute Category category) {
        categoryService.createCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoryPage(@PathVariable UUID id, Model model) {
        Category category = categoryService.findById(id);
        model.addAttribute("category", category);
        return "admin/category-edit";
    }

    @PostMapping("/categories/update/{id}")
    public String updateCategory(
            @PathVariable UUID id,
            @ModelAttribute Category category
    ) {
        categoryService.updateCategory(id, category);
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return "redirect:/admin/categories";
    }

    // Управление опциями категорий
    @GetMapping("/categories/{categoryId}/options")
    public String categoryOptions(@PathVariable UUID categoryId, Model model) {
        Category category = categoryService.findById(categoryId);
        model.addAttribute("category", category);
        model.addAttribute("options", category.getOptions());
        return "admin/category-options";
    }

    @PostMapping("/categories/{categoryId}/options/add")
    public String addCategoryOption(
            @PathVariable UUID categoryId,
            @RequestParam String value
    ) {
        categoryService.addOption(categoryId, value);
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/options/delete/{optionId}")
    public String deleteCategoryOption(@PathVariable UUID optionId) {
        categoryService.deleteOption(optionId);
        return "redirect:/admin/categories";
    }

    @GetMapping("/products/create")
    public String createProductPage(Model model) {

        model.addAttribute("product", new Product());
        model.addAttribute(
                "categoryOptions",
                categoryOptionRepository.findAll()
        );

        return "admin/product-create";
    }

    @PostMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @PostMapping("/products/create")
    public String createProduct(
            @ModelAttribute Product product,
            @RequestParam(required = false) List<UUID> categoryOptionIds,
            @RequestParam(required = false) List<String> imageUrls,
            @RequestParam(required = false) Integer mainImageIndex
    ) throws IOException {

        productService.createProduct(
                product,
                categoryOptionIds,
                imageUrls,
                mainImageIndex
        );
        return "redirect:/admin/products";
    }
}

