package org.hamza.book.dtos;

import java.util.List;

public record GameStateResponse(Long currentSectionId, int healthPoints, boolean isGameOver, boolean isVictory, String message, String sectionText, List<OptionResponse> optionResponses) {}