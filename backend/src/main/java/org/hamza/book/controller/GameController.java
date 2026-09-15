package org.hamza.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hamza.book.dtos.GameStateResponse;
import org.hamza.book.dtos.PlayerChoiceRequest;
import org.hamza.book.service.GameEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books/{bookId}/game")
@RequiredArgsConstructor
@Tag(name = "Game Engine", description = "Interactive endpoints for playing, making choices, saving, and resuming adventure book sessions")
public class GameController {

    private final GameEngineService gameEngineService;

    @Operation(summary = "Start a new game")
    @PostMapping("/start")
    public ResponseEntity<GameStateResponse> startGame(
            @PathVariable("bookId") Long bookId,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-user") String userId) {
        GameStateResponse initialState = gameEngineService.startGame(bookId);
        return ResponseEntity.ok(initialState);
    }

    @Operation(summary = "Save game progression")
    @PostMapping("/save")
    public ResponseEntity<Void> saveGame(
            @PathVariable("bookId") Long bookId,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-user") String userId,
            @RequestBody PlayerChoiceRequest request) {
        gameEngineService.saveGame(bookId, userId, request, false, false, "Game saved successfully.");
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Resume a saved game")
    @GetMapping("/resume")
    public ResponseEntity<GameStateResponse> resumeGame(
            @PathVariable("bookId") Long bookId,
            @RequestHeader(value = "X-User-Id", defaultValue = "guest-user") String userId) {
        GameStateResponse state = gameEngineService.resumeGame(bookId, userId);
        return ResponseEntity.ok(state);
    }

    @Operation(summary = "Make a player choice")
    @PostMapping("/choices")
    public ResponseEntity<GameStateResponse> makeChoice(
            @PathVariable("bookId") Long bookId,
            @RequestBody PlayerChoiceRequest request) {
        GameStateResponse nextState = gameEngineService.makeChoice(
                bookId,
                request.currentSectionId(),
                request.optionId(),
                request.currentHealth()
        );
        return ResponseEntity.ok(nextState);
    }
}