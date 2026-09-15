package org.hamza.book.dtos;

import org.hamza.book.enums.ConsequenceType;

public record ConsequenceResponse(ConsequenceType type, String value) {}
