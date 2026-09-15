import { Injectable } from '@angular/core';
import { HttpClient, HttpParams , HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import { BookEntity } from '../models/book-entity.model';
import { GameStateResponse } from '../models/game-state-response.model';

@Injectable({
  providedIn: 'root'
})
export class GameService {
  private apiUrl = '/api/books';

  constructor(private http: HttpClient) {}

  private getHeaders(): HttpHeaders {
    return new HttpHeaders({
      'X-User-Id': this.getOrCreateUserId(),
      'Content-Type': 'application/json'
    });
  }

  private getOrCreateUserId(): string {
    let userId = localStorage.getItem('adventure_user_id');
    if (!userId) {
      // crypto.randomUUID() guarantees absolute unicity across browsers and devices
      userId = crypto.randomUUID();
      localStorage.setItem('adventure_user_id', userId);
    }
    return userId;
  }

  startGame(bookId: number): Observable<GameStateResponse> {
    return this.http.post<GameStateResponse>(`${this.apiUrl}/${bookId}/game/start`, {}, {
      headers: this.getHeaders()
    });
  }

  resumeGame(bookId: number): Observable<GameStateResponse> {
    return this.http.get<GameStateResponse>(`${this.apiUrl}/${bookId}/game/resume`, {
      headers: this.getHeaders()
    });
  }

  getAllBooks(): Observable<any[]> {
     return this.http.get<any[]>(this.apiUrl);
  }

  importBook(bookData: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, bookData);
  }

  saveGame(bookId: number, currentSectionId: number, optionId: number | null, currentHealth: number): Observable<void> {
    const payload = { currentSectionId, optionId, currentHealth };
    return this.http.post<void>(`${this.apiUrl}/${bookId}/game/save`, payload, {
      headers: this.getHeaders()
    });
  }

  makeChoice(bookId: number, currentSectionId: number, optionId: number, currentHealth: number): Observable<GameStateResponse> {
    const payload = { currentSectionId, optionId, currentHealth };
    return this.http.post<GameStateResponse>(`${this.apiUrl}/${bookId}/game/choices`, payload, {
      headers: this.getHeaders()
    });
  }
}