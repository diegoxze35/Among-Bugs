package org.ipn.mx.among.bugs.mapper;

import org.ipn.mx.among.bugs.domain.dto.request.trivia.SubmitTriviaAttemptRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaAttemptResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.entity.Trivia;
import org.ipn.mx.among.bugs.domain.entity.TriviaAttempt;

public class TriviaAttemptMapper {

	private TriviaAttemptMapper() {}

	public static TriviaAttemptResponse toDto(TriviaAttempt attempt) {
		final Integer correctAnswers = attempt.getCorrectAnswers();
		final Integer totalQuestions = attempt.getTotalQuestions();
		return new TriviaAttemptResponse(
				attempt.getCorrectAnswers(),
				attempt.getTotalQuestions(),
				(correctAnswers.doubleValue() / totalQuestions.doubleValue()) * 100.0,
				attempt.getCompletionTimeSeconds(),
				attempt.getAttemptDate()
		);
	}

	public static TriviaAttempt toEntity(
			SubmitTriviaAttemptRequest request,
			Player player,
			Trivia trivia
	) {
		return TriviaAttempt.builder()
				.player(player)
				.trivia(trivia)
				.correctAnswers(request.correctAnswers())
				.totalQuestions(request.totalQuestions())
				.completionTimeSeconds(request.completionTimeSeconds())
				.build();
	}

}
