package org.ipn.mx.among.bugs.domain.dto.request.trivia;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateTriviaRequest(
		@Size(min = 5, max = 50)
		@NotNull
		String title,
		@Size(max = 200)
		@NotNull
		String description,
		@NotNull
		Boolean isPublic,
		Set<QuestionRequest> questions
) {
}
