package org.hamza.book.service;

import lombok.RequiredArgsConstructor;
import org.hamza.book.dtos.*;
import org.hamza.book.enums.SectionType;
import org.hamza.book.exception.InvalidBookException;
import org.hamza.book.model.*;
import org.hamza.book.repository.BookRepository;
import org.hamza.book.repository.GameSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GameEngineService {

    private final BookRepository bookRepository;
    private final GameSessionRepository gameSessionRepository;

    @Value("${game.initial-health:10}")
    private int initialHealth;

    @Transactional(readOnly = true)
    public GameStateResponse startGame(Long bookId) {
        BookEntity book = findBookOrThrow(bookId);

        SectionEntity beginSection = book.getSections().stream()
                .filter(s -> SectionType.BEGIN.equals(s.getType()))
                .findFirst()
                .orElseThrow(() -> new InvalidBookException("This book has no beginning section!"));

        return buildStateResponse(beginSection, initialHealth, false, false, "Your journey begins...");
    }

    @Transactional
    public GameStateResponse makeChoice(Long bookId, Long currentSectionOriginalId, Long chosenGotoId, int currentHealth) {
        BookEntity book = findBookOrThrow(bookId);

        SectionEntity targetSection = findSectionOrThrow(book, chosenGotoId);

        // Safely evaluate health modification through the chosen option
        int updatedHealth = calculateUpdatedHealth(book, currentSectionOriginalId, chosenGotoId, currentHealth);

        // Determine game status flags
        boolean isGameOver = updatedHealth <= 0;
        boolean isVictory = !isGameOver && isEndingSection(targetSection);

        String statusMessage = resolveStatusMessage(isGameOver, isVictory, targetSection);

        return buildStateResponse(targetSection, updatedHealth, isGameOver, isVictory, statusMessage);
    }

    @Transactional
    public void saveGame(Long bookId, String userId, PlayerChoiceRequest request, boolean isGameOver, boolean isVictory, String message) {
        validateUserId(userId);
        GameSessionEntity session = gameSessionRepository.findByBookIdAndUserId(bookId, userId)
                .orElse(new GameSessionEntity());

        session.setBookId(bookId);
        session.setUserId(userId);
        session.setCurrentSectionId(request.currentSectionId());
        session.setHealth(request.currentHealth());
        session.setGameOver(isGameOver);
        session.setVictory(isVictory);
        session.setMessage(message);

        gameSessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public GameStateResponse resumeGame(Long bookId, String userId) {
        validateUserId(userId);
        GameSessionEntity session = gameSessionRepository.findByBookIdAndUserId(bookId, userId)
                .orElseThrow(() -> new IllegalArgumentException("No saved game found for user " + userId + " on book ID: " + bookId));

        BookEntity book = findBookOrThrow(bookId);
        SectionEntity section = findSectionOrThrow(book, session.getCurrentSectionId());

        return buildStateResponse(section, session.getHealth(), session.isGameOver(), session.isVictory(), session.getMessage());
    }

    // --- Private Helper & Decoupling Methods ---

    private BookEntity findBookOrThrow(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new InvalidBookException("Book not found with ID: " + bookId));
    }

    private SectionEntity findSectionOrThrow(BookEntity book, Long sectionOriginalId) {
        return book.getSections().stream()
                .filter(s -> s.getOriginalSectionId().equals(sectionOriginalId))
                .findFirst()
                .orElseThrow(() -> new InvalidBookException("Section ID " + sectionOriginalId + " does not exist in this book."));
    }

    private int calculateUpdatedHealth(BookEntity bookEntity, Long currentSectionOriginalId, Long chosenGotoId, int currentHealth) {
        return bookEntity.getSections().stream()
                .filter(s -> s.getOriginalSectionId().equals(currentSectionOriginalId))
                .findFirst()
                .flatMap(currentSection -> currentSection.getOptions().stream()
                        .filter(o -> o.getGotoId().equals(chosenGotoId))
                        .findFirst())
                .map(OptionEntity::getConsequence)
                .map(consequence -> consequence.applyTo(currentHealth))
                .orElse(currentHealth);
    }

    private boolean isEndingSection(SectionEntity section) {
        return SectionType.END.equals(section.getType()) ||
                section.getOptions() == null ||
                section.getOptions().isEmpty();
    }

    private String resolveStatusMessage(boolean isGameOver, boolean isVictory, SectionEntity targetSection) {
        if (isGameOver) {
            return "You have succumbed to your injuries. Game Over.";
        }
        if (isVictory) {
            return "Congratulations! You have successfully completed your journey.";
        }
        if (SectionType.BEGIN.equals(targetSection.getType())) {
            return "Your journey begins...";
        }
        return "You proceed cautiously to the next area.";
    }

    private GameStateResponse buildStateResponse(SectionEntity section, int health, boolean isGameOver, boolean isVictory, String message) {
        List<OptionResponse> optionResponses = section.getOptions() == null ? List.of() :
                section.getOptions().stream()
                        .map(this::mapToOptionResponse)
                        .toList();

        return new GameStateResponse(
                section.getOriginalSectionId(),
                health,
                isGameOver,
                isVictory,
                message,
                section.getText(),
                optionResponses
        );
    }

    private OptionResponse mapToOptionResponse(OptionEntity option) {
        ConsequenceResponse consequenceResponse = null;

        if (option.getConsequence() != null) {
            consequenceResponse = new ConsequenceResponse(
                    option.getConsequence().getType(),
                    option.getConsequence().getValue()
            );
        }

        return new OptionResponse(
                option.getId(),
                option.getDescription(),
                option.getGotoId(),
                consequenceResponse
        );
    }

    private void validateUserId(String userId) {
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("User ID cannot be blank.");
        }
    }
}