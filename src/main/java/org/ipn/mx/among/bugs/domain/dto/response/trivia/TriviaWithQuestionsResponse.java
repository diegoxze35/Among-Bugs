package org.ipn.mx.among.bugs.domain.dto.response.trivia;

import java.util.List;

public record TriviaWithQuestionsResponse(
		Long id,
		String title,
		String description,
		Boolean isPublic,
		String creatorUsername,
		List<QuestionResponse> questions
) {
}
