package org.ipn.mx.among.bugs.domain.dto.request.trivia;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UpdateTriviaRequest(
		@Size(min = 5, max = 50)
		@NotNull
		String title,
		@NotNull
		@Min(value = 10)
		@Max(value = 100)
		Integer targetScore,
		@Size(max = 200)
		@NotNull
		String description,
		@NotNull
		Boolean isPublic,
		Set<UpdateQuestionRequest> questions
) {

}
