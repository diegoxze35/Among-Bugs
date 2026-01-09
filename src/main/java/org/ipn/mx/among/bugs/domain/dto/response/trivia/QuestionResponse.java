package org.ipn.mx.among.bugs.domain.dto.response.trivia;

import org.ipn.mx.among.bugs.domain.entity.enums.TrickType;

import java.util.List;

public record QuestionResponse(
		Long id,
		String questionText,
		List<QuestionOptionResponse> options,
		Integer timeLimit,
		TrickType trickType
) {
}
