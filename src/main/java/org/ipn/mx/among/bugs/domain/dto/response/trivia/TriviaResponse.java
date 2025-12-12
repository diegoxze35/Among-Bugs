package org.ipn.mx.among.bugs.domain.dto.response.trivia;

public record TriviaResponse(
		Long id,
		Integer targetScore,
		String title,
		String description,
		Boolean isPublic
) {
}
