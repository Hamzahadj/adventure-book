package org.hamza.book.service;


import lombok.RequiredArgsConstructor;
import org.hamza.book.dtos.BookImportDto;
import org.hamza.book.dtos.BookSummaryResponse;
import org.hamza.book.enums.ConsequenceType;
import org.hamza.book.exception.InvalidBookException;
import org.hamza.book.model.BookEntity;
import org.hamza.book.model.ConsequenceEmbeddable;
import org.hamza.book.model.OptionEntity;
import org.hamza.book.model.SectionEntity;
import org.hamza.book.parser.JsonBookParser;
import org.hamza.book.repository.BookRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

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
            List<SectionEntity> sectionEntities = dto.sections().stream().map(sDto -> {
                SectionEntity section = SectionEntity.builder()
                        .originalSectionId(sDto.id())
                        .text(sDto.text())
                        .type(sDto.type())
                        .book(book)
                        .build();

                if (sDto.options() != null) {
                    List<OptionEntity> optionEntities = sDto.options().stream().map(oDto -> {
                        ConsequenceEmbeddable consequence = null;
                        if (oDto.consequence() != null) {
                            consequence = ConsequenceEmbeddable.builder()
                                    .type(ConsequenceType.valueOf(String.valueOf(oDto.consequence().type())))
                                    .value(oDto.consequence().value())
                                    .build();
                        }

                        return OptionEntity.builder()
                                .description(oDto.description())
                                .gotoId(oDto.gotoId())
                                .consequence(consequence)
                                .section(section)
                                .build();
                    }).collect(Collectors.toList());

                    section.setOptions(optionEntities);
                }
                return section;
            }).collect(Collectors.toList());

            book.setSections(sectionEntities);
        }

        return book;
    }
}