package org.ipn.mx.among.bugs.domain.dto.request.trivia;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SubmitTriviaAttemptRequest(
	@NotNull(message = "El ID de la trivia es obligatorio")
	Long triviaId,

	@NotNull(message = "El número de respuestas correctas es obligatorio")
	@Min(value = 0, message = "El número de respuestas correctas no puede ser negativo")
	Integer correctAnswers,

	@NotNull(message = "El total de preguntas es obligatorio")
	@Min(value = 1, message = "Debe haber al menos una pregunta")
	Integer totalQuestions,

	@NotNull(message = "El tiempo de completación es obligatorio")
	@Min(value = 1, message = "El tiempo debe ser al menos 1 segundo")
	Long completionTimeSeconds
) {
}

