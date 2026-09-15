import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GameStateResponse } from '../../models/game-state-response.model';
import { OptionResponse } from '../../models/option-response.model';
import { GameService } from '../../services/game.service';

@Component({
  selector: 'app-game-play',
  templateUrl: './game-play.component.html',
  styleUrls: ['./game-play.component.css']
})
export class GamePlayComponent implements OnInit {
  bookId!: number;
  gameState?: GameStateResponse;
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private gameService: GameService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('bookId');
    if (idParam) {
      this.bookId = Number(idParam);
      this.startGame();
    } else {
      this.errorMessage = 'Invalid book reference provided.';
    }
  }

  startGame(): void {
    this.isLoading = true;
    this.errorMessage = '';

    this.gameService.startGame(this.bookId).subscribe({
      next: (state) => {
        this.gameState = state;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to initialize the game session.';
        this.isLoading = false;
      }
    });
  }

  onResumeGame() {
      this.gameService.resumeGame(this.bookId).subscribe({
        next: (state) => {
          this.gameState = state;
          console.log('Game resumed successfully!');
        },
        error: (err) => console.error('No saved game found or error resuming', err)
      });
    }

  onSaveGame() {
     if (!this.gameState) return;

      // Pass current progress payload matching your PlayerChoiceRequest
     this.gameService.saveGame(
       this.bookId,
       this.gameState.currentSectionId,
       null, // or last chosen option ID if applicable
       this.gameState.healthPoints
     ).subscribe({
       next: () => alert('Game progress saved successfully!'),
       error: (err) => console.error('Failed to save game', err)
     });
  }

  makeChoice(option: OptionResponse): void {
    if (!this.gameState) return;

    const currentSectionId = this.gameState.currentSectionId;
    const currentHealth = this.gameState.healthPoints;

    this.isLoading = true;
    this.errorMessage = '';

    this.gameService.makeChoice(this.bookId, currentSectionId, option.gotoId, currentHealth).subscribe({
      next: (nextState) => {
        this.gameState = nextState;
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to record choice transition.';
        this.isLoading = false;
      }
    });
  }

  returnToMenu(): void {
    this.router.navigate(['/']);
  }
}