package org.ipn.mx.among.bugs.domain.dto.response.trivia;

import java.util.List;

public record TriviaWithQuestionsResponse(
		Long id,
		Integer targetScore,
		String title,
		String description,
		Boolean isPublic,
		List<QuestionResponse> options
) {
}
