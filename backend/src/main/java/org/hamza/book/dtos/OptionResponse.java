package org.hamza.book.dtos;

public record OptionResponse(Long id, String description, Long gotoId, ConsequenceResponse consequence) {}
