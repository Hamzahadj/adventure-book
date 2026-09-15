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
            BookImport importDto = jsonBookParser.parse(inputStream);
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



    private BookEntity mapToEntity(BookImport bookImport) {
        BookEntity book = BookEntity.builder()
                .title(bookImport.title())
                .author(bookImport.author())
                .build();

        if (bookImport.sections() != null) {
            book.setSections(bookImport.sections().stream()
                    .map(sectionImport
                            -> mapSection(sectionImport, book))
                    .toList());
        }

        return book;
    }

    private SectionEntity mapSection(SectionImport sectionImport, BookEntity bookEntity) {
        SectionEntity section = SectionEntity.builder()
                .originalSectionId(sectionImport.id())
                .text(sectionImport.text())
                .type(sectionImport.type())
                .book(bookEntity)
                .build();

        if (sectionImport.options() != null) {
            section.setOptions(sectionImport.options().stream()
                    .map(optionDto -> mapOption(optionDto, section))
                    .toList());
        }

        return section;
    }

    private OptionEntity mapOption(@Valid OptionImport optionImport, SectionEntity sectionEntity) {
        return OptionEntity.builder()
                .description(optionImport.description())
                .gotoId(optionImport.gotoId())
                .consequence(mapConsequence(optionImport.consequence()))
                .section(sectionEntity)
                .build();
    }

    private ConsequenceEmbeddable mapConsequence(Consequence consequence) {
        if (consequence == null) {
            return null;
        }

        return ConsequenceEmbeddable.builder()
                .type(ConsequenceType.valueOf(consequence.type().toString()))
                .value(consequence.value())
                .build();
    }
}