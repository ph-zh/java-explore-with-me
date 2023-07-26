package ru.practicum.mainservice.service;

import ru.practicum.mainservice.dto.compilation.CompilationCreationDto;
import ru.practicum.mainservice.dto.compilation.CompilationDto;
import ru.practicum.mainservice.dto.compilation.UpdateCompilationRequestDto;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(CompilationCreationDto compilationCreationDto);

    void deleteCompilation(int compId);

    CompilationDto updateCompilation(int compId, UpdateCompilationRequestDto updateCompilationRequestDto);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(int compId);
}
