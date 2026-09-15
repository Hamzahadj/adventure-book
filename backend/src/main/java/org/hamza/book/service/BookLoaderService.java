package org.hamza.book.service;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hamza.book.dtos.*;
import org.hamza.book.enums.ConsequenceType;
import org.hamza.book.exception.InvalidBookException;
import org.hamza.book.model.BookEntity;
import org.hamza.book.model.ConsequenceEmbeddable;
import org.hamza.book.model.OptionEntity;
import org.hamza.book.model.SectionEntity;
import org.hamza.book.parser.JsonBookParser;
import org.hamza.book.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookLoaderService {

    private final JsonBookParser jsonBookParser;
    private final BookValidationService validationService;
    private final BookRepository bookRepository;

    @Transactional
    public BookEntity loadBookFromJson(String jsonContent) {
        try (InputStream inputStream = new ByteArrayInputStream(jsonContent.getBytes(StandardCharsets.UTF_8))) {
            BookImportDto importDto = jsonBookParser.parse(inputStream);
            BookEntity book = mapToEntity(importDto);
            validationService.validateBook(book);
            return bookRepository.save(book);
        } catch (IOException e) {
            throw new InvalidBookException("Failed to read JSON content: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<BookSummaryResponse> getAllBookSummaries() {
        return bookRepository.findAll().stream()
                .map(book -> new BookSummaryResponse(book.getId(), book.getTitle(), book.getAuthor()))
                .toList();
    }



    private BookEntity mapToEntity(BookImportDto dto) {
        BookEntity book = BookEntity.builder()
                .title(dto.title())
                .author(dto.author())
                .build();

        if (dto.sections() != null) {
            book.setSections(dto.sections().stream()
                    .map(sectionDto -> mapSection(sectionDto, book))
                    .toList());
        }

        return book;
    }

    private SectionEntity mapSection(SectionImportDto dto, BookEntity book) {
        SectionEntity section = SectionEntity.builder()
                .originalSectionId(dto.id())
                .text(dto.text())
                .type(dto.type())
                .book(book)
                .build();

        if (dto.options() != null) {
            section.setOptions(dto.options().stream()
                    .map(optionDto -> mapOption(optionDto, section))
                    .toList());
        }

        return section;
    }

    private OptionEntity mapOption(@Valid OptionImportDto dto, SectionEntity section) {
        return OptionEntity.builder()
                .description(dto.description())
                .gotoId(dto.gotoId())
                .consequence(mapConsequence(dto.consequence()))
                .section(section)
                .build();
    }

    private ConsequenceEmbeddable mapConsequence(ConsequenceDto dto) {
        if (dto == null) {
            return null;
        }

        return ConsequenceEmbeddable.builder()
                .type(ConsequenceType.valueOf(dto.type().toString()))
                .value(dto.value())
                .build();
    }
}