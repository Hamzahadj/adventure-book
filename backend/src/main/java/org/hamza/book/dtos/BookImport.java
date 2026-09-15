package org.hamza.book.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record BookImport(
        @NotBlank(message = "Book title is required")
        String title,

        @NotBlank(message = "Book author is required")
        String author,

        @Valid List<SectionImport> sections


) {}