package org.ipn.mx.among.bugs.service.impl;

import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.RankingResponse;
import org.ipn.mx.among.bugs.domain.entity.TriviaAttempt;
import org.ipn.mx.among.bugs.repository.trivia.TriviaAttemptRepository;
import org.ipn.mx.among.bugs.service.RankingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

	private final TriviaAttemptRepository triviaAttemptRepository;

	@Override
	@Transactional(readOnly = true)
	public List<RankingResponse> getGlobalRanking() {
		List<TriviaAttempt> attempts = triviaAttemptRepository.findGlobalRanking();
		return attempts.stream()
				.map(this::mapToRankingResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<RankingResponse> getRankingByTrivia(Long triviaId) {
		List<TriviaAttempt> attempts = triviaAttemptRepository.findTopAttemptsByTriviaId(triviaId);
		return attempts.stream()
				.map(this::mapToRankingResponse)
				.toList();
	}

	private RankingResponse mapToRankingResponse(TriviaAttempt attempt) {
		double percentage = (attempt.getCorrectAnswers().doubleValue() / attempt.getTotalQuestions()) * 100;

		return RankingResponse.builder()
				.attemptId(attempt.getId())
				.playerUsername(attempt.getPlayer().getUsername())
				.triviaTitle(attempt.getTrivia().getTitle())
				.correctAnswers(attempt.getCorrectAnswers())
				.totalQuestions(attempt.getTotalQuestions())
				.percentage(Math.round(percentage * 100.0) / 100.0)
				.completionTimeSeconds(attempt.getCompletionTimeSeconds())
				.attemptDate(attempt.getAttemptDate())
				.build();
	}
}

