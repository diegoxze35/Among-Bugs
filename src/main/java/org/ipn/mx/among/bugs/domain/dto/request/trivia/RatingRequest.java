package org.ipn.mx.among.bugs.domain.dto.request.trivia;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request para crear o actualizar un rating
 */
public record RatingRequest(
        @NotNull
        Long triviaId,
        @NotNull
        @Min(value = 1)
        @Max(value = 5)
        Short score,
        String comment // Opcional
) {
}

