package org.hamza.book.parser;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.hamza.book.dtos.BookImport;
import org.hamza.book.exception.InvalidBookException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class JsonBookParser {

    private final ObjectMapper objectMapper;

    public BookImport parse(InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, BookImport.class);
        } catch (IOException e) {
            throw new InvalidBookException("Failed to parse JSON book structure: " + e.getMessage());
        }
    }
}
