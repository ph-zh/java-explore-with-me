package ru.practicum.mainservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.category.CategoryCreationDto;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.mapper.CategoryMapper;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.pagination.OffsetBasedPageRequest;
import ru.practicum.mainservice.repository.CategoryRepository;
import ru.practicum.mainservice.repository.EventRepository;
import ru.practicum.mainservice.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto addCategory(CategoryCreationDto categoryCreationDto) {
        Category category = categoryRepository.save(CategoryMapper.toCategory(categoryCreationDto));

        return CategoryMapper.toCategoryDto(category);
    }

    @Transactional
    @Override
    public void deleteCategory(int catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Category with id=%d was not found", catId));
        }

        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("The category is not empty");
        }

        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(int catId, CategoryCreationDto categoryCreationDto) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", catId)));

        category.setName(categoryCreationDto.getName());

        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        Pageable pageable = new OffsetBasedPageRequest(from, size, Sort.by(Sort.Direction.ASC, "id"));
        return categoryRepository.findAll(pageable).getContent().stream()
                .map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategory(int catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", catId)));
        return CategoryMapper.toCategoryDto(category);
    }
}
