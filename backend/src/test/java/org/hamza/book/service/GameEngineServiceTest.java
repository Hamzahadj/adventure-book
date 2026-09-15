package org.hamza.book.service;

import org.hamza.book.dtos.GameStateResponse;
import org.hamza.book.enums.ConsequenceType;
import org.hamza.book.enums.SectionType;
import org.hamza.book.exception.InvalidBookException;
import org.hamza.book.model.BookEntity;
import org.hamza.book.model.ConsequenceEmbeddable;
import org.hamza.book.model.OptionEntity;
import org.hamza.book.model.SectionEntity;
import org.hamza.book.repository.BookRepository;
import org.hamza.book.repository.GameSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameEngineServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GameSessionRepository gameSessionRepository;

    @InjectMocks
    private GameEngineService gameEngineService;

    private BookEntity mockBook;

    @BeforeEach
    void setUp() {
        // Inject the @Value property manually for the test
        ReflectionTestUtils.setField(gameEngineService, "initialHealth", 10);

        // Set up a mock book with a beginning section and an option
        SectionEntity beginSection = new SectionEntity();
        beginSection.setOriginalSectionId(1L);
        beginSection.setText("You wake up in a dark room.");
        beginSection.setType(SectionType.BEGIN);

        OptionEntity option = new OptionEntity();
        option.setId(100L);
        option.setDescription("Open the door");
        option.setGotoId(2L);

        ConsequenceEmbeddable consequence = new ConsequenceEmbeddable();
        // Assuming your consequence reduces health or modifies it
        consequence.setType(ConsequenceType.LOSE_HEALTH);
        consequence.setValue("2");
        option.setConsequence(consequence);

        beginSection.setOptions(List.of(option));

        SectionEntity nextSection = new SectionEntity();
        nextSection.setOriginalSectionId(2L);
        nextSection.setText("You step out into a hallway.");
        nextSection.setType(SectionType.END);

        mockBook = new BookEntity();
        mockBook.setId(1L);
        mockBook.setSections(List.of(beginSection, nextSection));
    }

    @Test
    void startGame_ShouldReturnInitialStateSuccessfully() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));

        GameStateResponse response = gameEngineService.startGame(1L);

        assertNotNull(response);
        assertEquals(1L, response.currentSectionId());
        assertEquals(10, response.healthPoints());
        assertFalse(response.isGameOver());
        assertFalse(response.isVictory());
        assertEquals("You wake up in a dark room.", response.sectionText());
        assertFalse(response.optionResponses().isEmpty());
    }

    @Test
    void startGame_ShouldThrowException_WhenNoBeginSectionExists() {
        mockBook.getSections().get(0).setType(SectionType.NODE); // Remove BEGIN type
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));

        assertThrows(InvalidBookException.class, () -> {
            gameEngineService.startGame(1L);
        });
    }

    @Test
    void makeChoice_ShouldTransitionAndUpdateHealth() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));

        // Make choice from section 1 to gotoId 2 with 10 health
        GameStateResponse response = gameEngineService.makeChoice(1L, 1L, 2L, 10);

        assertNotNull(response);
        assertEquals(2L, response.currentSectionId());
        assertEquals(8, response.healthPoints()); // 10 - 2 damage from consequence
        assertEquals("You step out into a hallway.", response.sectionText());
    }
}