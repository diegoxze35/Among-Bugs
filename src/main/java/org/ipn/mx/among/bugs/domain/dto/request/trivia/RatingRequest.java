package org.ipn.mx.among.bugs.domain.dto.request.trivia;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Request para crear o actualizar un rating
 */
public record RatingRequest(
        @NotNull(message = "La calificación es obligatoria")
        @Min(value = 1, message = "La calificación mínima es 1 estrella")
        @Max(value = 5, message = "La calificación máxima es 5 estrellas")
        Short score,

        String comment // Opcional
) {
}

