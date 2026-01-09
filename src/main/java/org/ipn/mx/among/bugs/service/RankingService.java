package org.ipn.mx.among.bugs.service;

import org.ipn.mx.among.bugs.domain.dto.response.trivia.RankingResponse;

import java.util.List;

public interface RankingService {
	List<RankingResponse> getGlobalRanking();
	List<RankingResponse> getRankingByTrivia(Long triviaId);
}

