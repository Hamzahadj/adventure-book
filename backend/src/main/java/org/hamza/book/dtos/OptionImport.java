package org.hamza.book.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OptionImport(
        @NotBlank(message = "Option description is required")
        String description,

        @NotNull(message = "Target section gotoId is required")
        Long gotoId,

        @Valid // Validates inner fields if consequence is provided
        Consequence consequence
) {}