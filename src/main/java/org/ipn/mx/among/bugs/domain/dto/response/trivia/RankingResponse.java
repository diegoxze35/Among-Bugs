package org.ipn.mx.among.bugs.domain.dto.response.trivia;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record RankingResponse(
	Long attemptId,
	String playerUsername,
	String triviaTitle,
	Integer correctAnswers,
	Integer totalQuestions,
	Double percentage,
	Long completionTimeSeconds,
	LocalDateTime attemptDate
) {
}

