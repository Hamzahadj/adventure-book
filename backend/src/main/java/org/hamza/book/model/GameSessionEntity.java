package org.hamza.book.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_sessions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"book_id", "user_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long bookId;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private Long currentSectionId;

    @Column(nullable = false)
    private int health;

    @Column(nullable = false)
    private boolean gameOver;

    @Column(nullable = false)
    private boolean victory;

    @Column(length = 500)
    private String message;
}