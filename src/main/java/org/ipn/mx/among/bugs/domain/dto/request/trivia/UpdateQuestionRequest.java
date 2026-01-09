package org.ipn.mx.among.bugs.domain.dto.request.trivia;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.ipn.mx.among.bugs.domain.entity.enums.TrickType;

import java.util.List;

public record UpdateQuestionRequest(
		@NotNull
		Long id,
		@Size(min = 5, max = 200)
		@NotNull
		String questionText,
		@NotNull
		List<QuestionOptionRequest> options,
		@Min(value = 10)
		@Max(value = 120)
		Integer timeLimit,
		TrickType trickType
) {
}
