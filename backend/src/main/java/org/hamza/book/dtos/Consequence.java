package org.hamza.book.dtos;

import jakarta.validation.constraints.NotNull;
import org.hamza.book.enums.ConsequenceType;

public record Consequence(
        @NotNull(message = "Consequence type is required")
        ConsequenceType type,

        @NotNull(message = "Consequence value is required")
        String value
) {}