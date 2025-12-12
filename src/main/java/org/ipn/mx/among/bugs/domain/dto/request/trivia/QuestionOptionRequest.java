package org.ipn.mx.among.bugs.domain.dto.request.trivia;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record QuestionOptionRequest(
		@Size(min = 5, max = 200)
		@NotNull
		String text,
		@NotNull
		Boolean isCorrect
) {
}
