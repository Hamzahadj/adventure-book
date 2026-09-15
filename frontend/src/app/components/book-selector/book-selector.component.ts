import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BookEntity } from '../../models/book-entity.model';
import { GameService } from '../../services/game.service';

@Component({
  selector: 'app-book-selector',
  templateUrl: './book-selector.component.html',
  styleUrls: ['./book-selector.component.css']
})
export class BookSelectorComponent implements OnInit {
  books: BookEntity[] = [];
  rawJsonInput: string = '';
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private  gameService: GameService,
    private  router: Router
  ) {}

  ngOnInit(): void {
    this.loadBooks();
  }

  loadBooks(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.gameService.getAllBooks().subscribe({
      next: (data) => {
        this.books = data;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load adventure books from server.';
        this.isLoading = false;
      }
    });
  }

  importBook(): void {
    if (!this.rawJsonInput.trim()) {
      this.errorMessage = 'Please provide a valid JSON string.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.gameService.importBook(this.rawJsonInput).subscribe({
      next: () => {
        this.rawJsonInput = '';
        this.loadBooks();
      },
      error: () => {
        this.errorMessage = 'Failed to import book. Check structural validity and schema rules.';
        this.isLoading = false;
      }
    });
  }

  playBook(bookId: number): void {
    this.router.navigate(['/game', bookId]);
  }
}