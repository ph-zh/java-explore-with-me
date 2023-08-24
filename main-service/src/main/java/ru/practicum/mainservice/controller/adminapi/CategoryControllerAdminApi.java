package ru.practicum.mainservice.controller.adminapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.category.CategoryCreationDto;
import ru.practicum.mainservice.dto.category.CategoryDto;
import ru.practicum.mainservice.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
public class CategoryControllerAdminApi {
    private final CategoryService categoryService;

    @PostMapping("/categories")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid CategoryCreationDto categoryCreationDto) {
        return categoryService.addCategory(categoryCreationDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @Positive int catId) {
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable @Positive int catId,
                                      @RequestBody @Valid CategoryCreationDto categoryCreationDto) {
        return categoryService.updateCategory(catId, categoryCreationDto);
    }
}
