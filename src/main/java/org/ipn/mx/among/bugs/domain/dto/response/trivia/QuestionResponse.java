package org.ipn.mx.among.bugs.domain.dto.response.trivia;

import java.util.List;

public record QuestionResponse(
		Long id,
		String questionText,
		List<QuestionOptionResponse> options
) {
}
