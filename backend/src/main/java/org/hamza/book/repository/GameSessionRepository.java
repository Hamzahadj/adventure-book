package org.hamza.book.repository;

import org.hamza.book.model.GameSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSessionEntity, Long> {
    Optional<GameSessionEntity> findByBookIdAndUserId(Long bookId, String userId);
}