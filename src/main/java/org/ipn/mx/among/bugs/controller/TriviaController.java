package org.ipn.mx.among.bugs.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.CreateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.SubmitTriviaAttemptRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.UpdateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaAttemptResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaRankingResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaWithQuestionsResponse;
import org.ipn.mx.among.bugs.service.PdfTriviaService;
import org.ipn.mx.among.bugs.service.TriviaService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@RestController
@RequestMapping("/api/trivia")
@RequiredArgsConstructor
public class TriviaController {

	private final TriviaService triviaService;
	private final PdfTriviaService pdfTriviaService;

	@GetMapping
	public ResponseEntity<Set<TriviaResponse>> getAllTriviaByPlayerId(@AuthenticationPrincipal Long playerId) {
		Set<TriviaResponse> response = triviaService.getAllTriviaByPlayerId(playerId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/public")
	public ResponseEntity<Set<TriviaResponse>> getAllPublicTrivia(@ParameterObject @PageableDefault(sort = "title") Pageable pageable) {
		Set<TriviaResponse> response = triviaService.getAllPublicTrivia(pageable);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{triviaId}")
	public ResponseEntity<TriviaWithQuestionsResponse> getTriviaWithQuestions(@PathVariable Long triviaId) {
		TriviaWithQuestionsResponse response = triviaService.getTriviaWithQuestions(triviaId);
		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "/report")
	public ResponseEntity<byte[]> createPdfReport(@AuthenticationPrincipal Long playerId) {
		final Set<TriviaWithQuestionsResponse> allPlayerTrivia = triviaService.getAllTriviaWithQuestionsByPlayerId(playerId);
		if (allPlayerTrivia.isEmpty()) {
			return ResponseEntity.noContent().build();
		} else {
			final byte[] pdfBytes = pdfTriviaService.generateTriviaReport(allPlayerTrivia);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
			final String fileName = "report_" + LocalDateTime.now().format(formatter) + ".pdf";
			return ResponseEntity
					.ok()
					.contentType(MediaType.APPLICATION_PDF)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
					.body(pdfBytes);
		}
	}

	@GetMapping(value = "/stats/report")
	public ResponseEntity<byte[]> downloadPlayerStatsReport(@AuthenticationPrincipal Long playerId) {
		try {
			final byte[] pdfBytes = pdfTriviaService.generatePlayerStatsReport(playerId);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
			final String fileName = "estadisticas_" + LocalDateTime.now().format(formatter) + ".pdf";
			return ResponseEntity
					.ok()
					.contentType(MediaType.APPLICATION_PDF)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
					.body(pdfBytes);
		} catch (RuntimeException e) {
			return ResponseEntity.noContent().build();
		}
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TriviaWithQuestionsResponse createTrivia(
			@Valid @RequestBody CreateTriviaRequest request,
			@AuthenticationPrincipal Long playerId
	) {
		return triviaService.createTrivia(playerId, request);
	}

	@PostMapping("/attempt")
	@ResponseStatus(HttpStatus.CREATED)
	public TriviaAttemptResponse submitAttempt(
			@Valid @RequestBody SubmitTriviaAttemptRequest request,
			@AuthenticationPrincipal Long playerId
	) {
		return triviaService.submitAttempt(playerId, request);
	}

	@GetMapping("/{triviaId}/rankings")
	public ResponseEntity<TriviaRankingResponse> getTriviaRankings(@PathVariable Long triviaId) {
		TriviaRankingResponse response = triviaService.getTriviaRankings(triviaId);
		return ResponseEntity.ok(response);
	}

	@PutMapping
	public TriviaWithQuestionsResponse updateTrivia(@Valid @RequestBody UpdateTriviaRequest request) {
		return triviaService.updateTrivia(request);
	}

	@DeleteMapping("/{triviaId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTrivia(@PathVariable Long triviaId) {
		triviaService.deleteTrivia(triviaId);
	}

}
