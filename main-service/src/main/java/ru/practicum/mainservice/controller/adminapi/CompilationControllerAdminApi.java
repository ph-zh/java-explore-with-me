package ru.practicum.mainservice.controller.adminapi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.compilation.CompilationCreationDto;
import ru.practicum.mainservice.dto.compilation.CompilationDto;
import ru.practicum.mainservice.dto.compilation.UpdateCompilationRequestDto;
import ru.practicum.mainservice.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
public class CompilationControllerAdminApi {
    private final CompilationService compilationService;

    @PostMapping("/compilations")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CompilationDto createCompilation(@RequestBody @Valid CompilationCreationDto compilationCreationDto) {
        return compilationService.createCompilation(compilationCreationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive int compId) {
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable @Positive int compId,
                                            @RequestBody @Valid UpdateCompilationRequestDto updateCompilationRequestDto) {
        return compilationService.updateCompilation(compId, updateCompilationRequestDto);
    }
}
