package org.ipn.mx.among.bugs.domain.dto.request.trivia;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record QuestionRequest(
		@Size(min = 5, max = 200)
		@NotNull
		String questionText,
		@NotNull
		List<QuestionOptionRequest> options
) {
}
