package org.ipn.mx.among.bugs.domain.dto.response.trivia;

import java.time.LocalDateTime;

public record TriviaAttemptResponse(
	Long id,
	String playerUsername,
	Long playerId,
	Integer correctAnswers,
	Integer totalQuestions,
	Double accuracyPercentage,
	Long completionTimeSeconds,
	LocalDateTime attemptDate
) {
	public TriviaAttemptResponse(
		Long id,
		String playerUsername,
		Long playerId,
		Integer correctAnswers,
		Integer totalQuestions,
		Long completionTimeSeconds,
		LocalDateTime attemptDate
	) {
		this(
			id,
			playerUsername,
			playerId,
			correctAnswers,
			totalQuestions,
			(correctAnswers.doubleValue() / totalQuestions.doubleValue()) * 100.0,
			completionTimeSeconds,
			attemptDate
		);
	}
}

