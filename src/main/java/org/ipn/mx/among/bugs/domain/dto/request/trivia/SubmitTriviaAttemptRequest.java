package org.ipn.mx.among.bugs.domain.dto.request.trivia;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SubmitTriviaAttemptRequest(
		/*Jakarta validations ya traduce los mensajes con el header Accept-Language*/
		@NotNull
		Long triviaId,

		@NotNull
		@Min(value = 0)
		Integer correctAnswers,

		@NotNull
		@Min(value = 1)
		Integer totalQuestions,

		@NotNull
		@Min(value = 1)
		Long completionTimeSeconds
) {
}

