package org.ipn.mx.among.bugs.domain.dto.response.trivia;

import java.time.LocalDateTime;

public record TriviaAttemptResponse(
		Integer correctAnswers,
		Integer totalQuestions,
		Double accuracyPercentage,
		Long completionTimeSeconds,
		LocalDateTime attemptDate
) {

}

