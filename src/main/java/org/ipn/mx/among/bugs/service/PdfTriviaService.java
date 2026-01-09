package org.ipn.mx.among.bugs.service;

import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaWithQuestionsResponse;

import java.util.Set;

public interface PdfTriviaService {
	byte[] generateTriviaReport(Set<TriviaWithQuestionsResponse> trivia);
	byte[] generatePlayerStatsReport(Long playerId);
}
