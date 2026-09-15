package org.hamza.book.service;

import org.hamza.book.enums.SectionType;
import org.hamza.book.exception.InvalidBookException;
import org.hamza.book.model.BookEntity;
import org.hamza.book.model.OptionEntity;
import org.hamza.book.model.SectionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class BookValidationServiceTest {

    @InjectMocks
    private BookValidationService validationService;

    @Test
    void validateBook_ShouldPass_WhenBookIsValid() {
        BookEntity book = new BookEntity();

        SectionEntity beginSection = new SectionEntity();
        beginSection.setOriginalSectionId(1L);
        beginSection.setType(SectionType.BEGIN);

        OptionEntity option = new OptionEntity();
        option.setGotoId(2L);
        option.setDescription("Go to end");
        beginSection.setOptions(List.of(option));

        SectionEntity endSection = new SectionEntity();
        endSection.setOriginalSectionId(2L);
        endSection.setType(SectionType.END);
        endSection.setOptions(List.of()); // Ending section

        book.setSections(List.of(beginSection, endSection));

        // Should not throw any exception
        assertDoesNotThrow(() -> validationService.validateBook(book));
    }

    @Test
    void validateBook_ShouldThrowException_WhenSectionsEmpty() {
        BookEntity book = new BookEntity();
        book.setSections(List.of());

        InvalidBookException exception = assertThrows(InvalidBookException.class, () -> {
            validationService.validateBook(book);
        });
        assertEquals("Book must contain at least one section.", exception.getMessage());
    }

    @Test
    void validateBook_ShouldThrowException_WhenNoBeginSection() {
        BookEntity book = new BookEntity();

        SectionEntity nodeSection = new SectionEntity();
        nodeSection.setOriginalSectionId(1L);
        nodeSection.setType(SectionType.NODE);

        OptionEntity option = new OptionEntity();
        option.setGotoId(1L);
        nodeSection.setOptions(List.of(option));

        book.setSections(List.of(nodeSection));

        InvalidBookException exception = assertThrows(InvalidBookException.class, () -> {
            validationService.validateBook(book);
        });
        assertTrue(exception.getMessage().contains("beginning section"));
    }

    @Test
    void validateBook_ShouldThrowException_WhenMultipleBeginSections() {
        BookEntity book = new BookEntity();

        SectionEntity begin1 = new SectionEntity();
        begin1.setOriginalSectionId(1L);
        begin1.setType(SectionType.BEGIN);
        begin1.setOptions(List.of());

        SectionEntity begin2 = new SectionEntity();
        begin2.setOriginalSectionId(2L);
        begin2.setType(SectionType.BEGIN);
        begin2.setOptions(List.of());

        book.setSections(List.of(begin1, begin2));

        InvalidBookException exception = assertThrows(InvalidBookException.class, () -> {
            validationService.validateBook(book);
        });
        assertTrue(exception.getMessage().contains("beginning section"));
    }

    @Test
    void validateBook_ShouldThrowException_WhenNoEndingSection() {
        BookEntity book = new BookEntity();

        SectionEntity begin = new SectionEntity();
        begin.setOriginalSectionId(1L);
        begin.setType(SectionType.BEGIN);

        OptionEntity option = new OptionEntity();
        option.setGotoId(1L);
        begin.setOptions(List.of(option)); // Points back to itself, no ending exit

        book.setSections(List.of(begin));

        InvalidBookException exception = assertThrows(InvalidBookException.class, () -> {
            validationService.validateBook(book);
        });
        assertEquals("Book must have at least one ending section.", exception.getMessage());
    }

    @Test
    void validateBook_ShouldThrowException_WhenNonEndingSectionHasNoOptions() {
        BookEntity book = new BookEntity();

        SectionEntity begin = new SectionEntity();
        begin.setOriginalSectionId(1L);
        begin.setType(SectionType.BEGIN);
        begin.setOptions(List.of()); // Invalid because it's BEGIN (non-ending) and has no options

        book.setSections(List.of(begin));

        InvalidBookException exception = assertThrows(InvalidBookException.class, () -> {
            validationService.validateBook(book);
        });
        assertTrue(exception.getMessage().contains("has no options"));
    }

}