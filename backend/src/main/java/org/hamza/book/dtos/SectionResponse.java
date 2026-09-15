package org.hamza.book.dtos;

import java.util.List;

public record SectionResponse(Long id, String text, String type, List<OptionResponse> options) {}
