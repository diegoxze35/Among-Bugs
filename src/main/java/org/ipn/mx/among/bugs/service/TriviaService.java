package org.ipn.mx.among.bugs.service;

import java.util.Set;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.CreateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.UpdateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaWithQuestionsResponse;
import org.springframework.data.domain.Pageable;

public interface TriviaService {
	TriviaWithQuestionsResponse createTrivia(Long playerId, CreateTriviaRequest trivia);
	TriviaWithQuestionsResponse updateTrivia(UpdateTriviaRequest trivia, Long triviaId);
	Set<TriviaResponse> getAllTriviaByPlayerId(Long playerId);
	Set<TriviaResponse> getAllPublicTrivia(Pageable pageable);
	void deleteTrivia(Long triviaId);
}
