package org.hamza.book.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hamza.book.dtos.BookSummaryResponse;
import org.hamza.book.model.BookEntity;
import org.hamza.book.repository.BookRepository;
import org.hamza.book.service.BookLoaderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book Management", description = "Endpoints for loading, listing, and validating adventure books")
public class BookController {

    private final BookLoaderService bookLoaderService;
    private final BookRepository bookRepository;

    @Operation(summary = "Upload and import a new book", description = "Parses a raw JSON book string, validates its integrity, and persists it to the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book successfully imported and validated"),
            @ApiResponse(responseCode = "400", description = "Invalid book structure or validation failure")
    })
    @PostMapping
    public ResponseEntity<BookEntity> importBook(@Valid @RequestBody String jsonContent) {
        BookEntity savedBook = bookLoaderService.loadBookFromJson(jsonContent);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }


    @Operation(summary = "Get all book summaries", description = "Retrieves a lightweight list of all available books for the home page search/filter view.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved book summaries")
    @GetMapping
    public ResponseEntity<List<BookSummaryResponse>> getAllBooks() {
        List<BookSummaryResponse> summaries = bookLoaderService.getAllBookSummaries();
        return ResponseEntity.ok(summaries);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookEntity> getBookById(@PathVariable Long bookId) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + bookId));
        return ResponseEntity.ok(book);
    }
}
