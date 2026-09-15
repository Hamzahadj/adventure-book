package org.hamza.book.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.hamza.book.enums.ConsequenceType;
import org.hamza.book.exception.InvalidBookException;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsequenceEmbeddable {
    @Enumerated(EnumType.STRING)
    private ConsequenceType type;
    private String value;

    public int applyTo(int currentHealth) {
        if (type == null || type == ConsequenceType.NONE || value == null) {
            return currentHealth;
        }

        try {
            int numericValue = Integer.parseInt(value);
            return switch (type) {
                case LOSE_HEALTH -> Math.max(0, currentHealth - numericValue);
                case GAIN_HEALTH -> currentHealth + numericValue;
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };
        } catch (NumberFormatException e) {
            throw new InvalidBookException("Invalid numeric format for consequence value: '" + value + "'");
        }
    }
}
