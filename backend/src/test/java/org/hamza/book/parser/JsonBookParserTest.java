package org.hamza.book.parser;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamza.book.dtos.BookImportDto;
import org.hamza.book.exception.InvalidBookException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JsonBookParserTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JsonBookParser jsonBookParser;

    @Test
    void parse_ShouldReturnBookImportDto_WhenJsonIsValid() throws Exception {
        // Arrange
        String jsonContent = "{\"title\": \"Test Book\", \"sections\": []}";
        InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8));

        BookImportDto mockDto = new BookImportDto("Test Book", null, null);
        when(objectMapper.readValue(any(InputStream.class), eq(BookImportDto.class)))
                .thenReturn(mockDto);

        // Act
        BookImportDto result = jsonBookParser.parse(inputStream);

        // Assert
        assertNotNull(result);
        assertEquals("Test Book", result.title());
    }

    @Test
    void parse_ShouldThrowInvalidBookException_WhenIoExceptionOccurs() throws Exception {
        // Arrange
        InputStream inputStream = new ByteArrayInputStream("invalid json".getBytes(StandardCharsets.UTF_8));

        when(objectMapper.readValue(any(InputStream.class), eq(BookImportDto.class)))
                .thenThrow(new IOException("Malformed JSON"));

        // Act & Assert
        InvalidBookException exception = assertThrows(InvalidBookException.class, () -> {
            jsonBookParser.parse(inputStream);
        });

        assertTrue(exception.getMessage().contains("Failed to parse JSON book structure: Malformed JSON"));
    }
}