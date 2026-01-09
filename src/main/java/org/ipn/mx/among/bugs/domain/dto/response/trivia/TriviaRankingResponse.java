package org.ipn.mx.among.bugs.domain.dto.response.trivia;

import java.util.List;

public record TriviaRankingResponse(
	Long triviaId,
	String triviaTitle,
	Integer totalQuestions,
	List<TriviaAttemptResponse> rankings
) {
}

