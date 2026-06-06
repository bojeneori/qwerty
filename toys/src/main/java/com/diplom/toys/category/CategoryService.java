package com.diplom.toys.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryOptionRepository categoryOptionRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Category findById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Категория не найдена: " + id));
    }

    @Transactional
    public void createCategory(Category category) {
        category.setId(UUID.randomUUID());
        categoryRepository.save(category);
    }

    @Transactional
    public void updateCategory(UUID id, Category category) {
        Category existingCategory = findById(id);
        existingCategory.setName(category.getName());
        categoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Category category = findById(id);
        categoryRepository.delete(category);
    }

    @Transactional
    public void addOption(UUID categoryId, String value) {
        Category category = findById(categoryId);

        CategoryOption option = new CategoryOption();
        option.setId(UUID.randomUUID());
        option.setCategory(category);
        option.setValue(value);

        categoryOptionRepository.save(option);
    }

    @Transactional
    public UUID deleteOption(UUID optionId) {
        CategoryOption option = categoryOptionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Опция не найдена: " + optionId));
        UUID categoryId = option.getCategory().getId();
        categoryOptionRepository.delete(option);
        return categoryId;
    }
    public List<Category> getAllWithOptions() {
        return categoryRepository.findAllWithOptions();
    }
}
