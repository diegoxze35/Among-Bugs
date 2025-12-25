package org.ipn.mx.among.bugs.service;

import java.util.Set;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.CreateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.UpdateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaWithQuestionsResponse;
import org.springframework.data.domain.Pageable;

public interface TriviaService {
	Set<TriviaResponse> getAllTriviaByPlayerId(Long playerId);
	Set<TriviaResponse> getAllPublicTrivia(Pageable pageable);
	Set<TriviaWithQuestionsResponse> getAllTriviaWithQuestionsByPlayerId(Long playerId);
	TriviaWithQuestionsResponse createTrivia(Long playerId, CreateTriviaRequest request);
	TriviaWithQuestionsResponse updateTrivia(UpdateTriviaRequest request);
	void deleteTrivia(Long triviaId);
}
