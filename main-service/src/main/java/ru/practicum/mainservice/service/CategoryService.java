package ru.practicum.mainservice.service;

import ru.practicum.mainservice.dto.category.CategoryCreationDto;
import ru.practicum.mainservice.dto.category.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryCreationDto categoryCreationDto);

    void deleteCategory(int catId);

    CategoryDto updateCategory(int catId, CategoryCreationDto categoryCreationDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(int catId);
}
