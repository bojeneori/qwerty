package com.diplom.toys.product;

import com.diplom.toys.category.CategoryOption;
import com.diplom.toys.category.CategoryOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryOptionRepository categoryOptionRepository;

    @Transactional
    public void createProduct(
            Product product,
            List<UUID> categoryOptionIds,
            List<String> imageUrls,
            Integer mainImageIndex
    ) throws IOException {

        product.setId(UUID.randomUUID());
        product.setIsActive(true);

        if (categoryOptionIds != null) {
            product.setCategoryOptions(
                    categoryOptionRepository.findAllById(categoryOptionIds)
            );
        }

        productRepository.save(product);

        List<ProductImage> savedImages = new ArrayList<>();

        if (imageUrls != null) {
            for (String url : imageUrls) {
                if (url == null || url.isBlank()) {
                    continue;
                }

                ProductImage image = new ProductImage();
                image.setId(UUID.randomUUID());
                image.setProduct(product);
                image.setImageUrl(url);

                savedImages.add(
                        productImageRepository.save(image)
                );
            }
        }

        // главное изображение
        if (!savedImages.isEmpty()
                && mainImageIndex != null
                && mainImageIndex < savedImages.size()) {

            product.setMainImage(
                    savedImages.get(mainImageIndex).getId()
            );

            productRepository.save(product);
        }
    }

    public Product getById(UUID id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Product findById(UUID id) {
        return productRepository.findByIdWithImages(id)
                .orElseThrow(() -> new RuntimeException("Товар не найден: " + id));
    }

    @Transactional
    public void updateProduct(UUID id, Product product, List<UUID> categoryOptionIds,
                              List<String> imageUrls, MultipartFile[] images,
                              Integer mainImageIndex) throws IOException {

        Product existingProduct = findById(id);

        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setEditionSize(product.getEditionSize());
        existingProduct.setQuantityInStock(product.getQuantityInStock());
        existingProduct.setIsActive(product.getIsActive());

        if (categoryOptionIds != null) {
            List<CategoryOption> categoryOptions = categoryOptionRepository.findAllById(categoryOptionIds);
            existingProduct.setCategoryOptions(categoryOptions);
        }

        if (existingProduct.getImages() != null && !existingProduct.getImages().isEmpty()) {
            productImageRepository.deleteAll(existingProduct.getImages());
            existingProduct.getImages().clear();
        }

        List<ProductImage> newImages = new ArrayList<>();
        if (imageUrls != null) {
            for (String url : imageUrls) {
                if (url != null && !url.isBlank()) {
                    ProductImage newImage = new ProductImage();
                    newImage.setId(UUID.randomUUID());
                    newImage.setProduct(existingProduct);
                    newImage.setImageUrl(url);
                    newImages.add(productImageRepository.save(newImage));
                }
            }
        }

        existingProduct.setImages(newImages);

        if (mainImageIndex != null && mainImageIndex >= 0 && mainImageIndex < newImages.size()) {
            existingProduct.setMainImage(newImages.get(mainImageIndex).getId());
        } else if (!newImages.isEmpty()) {
            existingProduct.setMainImage(newImages.get(0).getId());
        } else {
            existingProduct.setMainImage(null);
        }

        productRepository.save(existingProduct);
    }

    public void toggleActiveStatus(UUID id) {
        Product product = findById(id);
        product.setIsActive(!product.getIsActive());
        productRepository.save(product);
    }

    public List<Product> findProducts(String search, List<UUID> filters) {

        List<Product> products;

        if (filters != null && !filters.isEmpty()) {
            products = productRepository.findByAllCategoryOptions(filters, filters.size());
        } else {
            products = productRepository.findAll();
        }

        // Фильтруем только активные товары
        products = products.stream()
                .filter(Product::getIsActive)
                .toList();

        if (search != null && !search.isBlank()) {
            String lower = search.toLowerCase();

            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(lower))
                    .toList();
        }

        return products;
    }

    @Transactional
    public void deleteProduct(UUID id) {
        // Сначала удаляем все изображения
        productImageRepository.deleteByProductId(id);

        // Затем удаляем сам товар
        productRepository.deleteById(id);
    }
}