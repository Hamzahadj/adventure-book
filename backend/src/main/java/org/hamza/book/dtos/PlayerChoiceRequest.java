package org.hamza.book.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlayerChoiceRequest(@NotNull Long bookId, @NotNull Long currentSectionId, @NotNull Long optionId, @Min(0) int currentHealth) {}