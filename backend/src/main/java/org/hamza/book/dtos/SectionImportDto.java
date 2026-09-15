package org.hamza.book.dtos;

import org.hamza.book.enums.SectionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record SectionImportDto(
        @NotNull(message = "Section ID is required")
        Long id,

        @NotBlank(message = "Section text narrative is required")
        String text,

        @NotNull(message = "Section type is required")
        SectionType type,

        @NotNull(message = "Options list cannot be null")
        List<@Valid OptionImportDto> options
) {}