import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { AppComponent } from './app.component';
import { BookSelectorComponent } from './components/book-selector/book-selector.component';
import { GamePlayComponent } from './components/game-play/game-play.component';

const routes: Routes = [
  { path: '', component: BookSelectorComponent },
  { path: 'game/:bookId', component: GamePlayComponent },
  { path: '**', redirectTo: '' }
];

@NgModule({
  declarations: [
    AppComponent,
    BookSelectorComponent,
    GamePlayComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    FormsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }