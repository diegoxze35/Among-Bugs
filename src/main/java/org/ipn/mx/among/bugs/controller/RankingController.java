package org.ipn.mx.among.bugs.controller;

import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.RankingResponse;
import org.ipn.mx.among.bugs.service.RankingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {

	private final RankingService rankingService;

	@GetMapping
	public ResponseEntity<List<RankingResponse>> getGlobalRanking() {
		List<RankingResponse> rankings = rankingService.getGlobalRanking();
		return ResponseEntity.ok(rankings);
	}

	@GetMapping("/trivia/{triviaId}")
	public ResponseEntity<List<RankingResponse>> getRankingByTrivia(@PathVariable Long triviaId) {
		List<RankingResponse> rankings = rankingService.getRankingByTrivia(triviaId);
		return ResponseEntity.ok(rankings);
	}
}

