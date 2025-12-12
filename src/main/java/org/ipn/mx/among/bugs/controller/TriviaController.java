package org.ipn.mx.among.bugs.controller;

import jakarta.validation.Valid;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.CreateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.UpdateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaWithQuestionsResponse;
import org.ipn.mx.among.bugs.service.TriviaService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trivia")
@RequiredArgsConstructor
public class TriviaController {

	private final TriviaService triviaService;

	//We will remove this parameter because we'll get the player id from JWT token after player authentication
	@PostMapping("/{playerId}")
	@ResponseStatus(HttpStatus.CREATED)
	public TriviaWithQuestionsResponse createTrivia(
			@Valid @RequestBody CreateTriviaRequest request,
			@PathVariable Long playerId
	) {
		return triviaService.createTrivia(playerId, request);
	}

	@PutMapping("/{triviaId}")
	public TriviaWithQuestionsResponse updateTrivia(@Valid @RequestBody UpdateTriviaRequest request, @PathVariable Long triviaId) {
		return triviaService.updateTrivia(request, triviaId);
	}

	@DeleteMapping("/{triviaId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTrivia(@PathVariable Long triviaId) {
		triviaService.deleteTrivia(triviaId);
	}

	@GetMapping
	public Set<TriviaResponse> getAllPublicTrivia(@ParameterObject @PageableDefault(sort = "title") Pageable pageable) {
		return triviaService.getAllPublicTrivia(pageable);
	}

	//We will remove this parameter because we'll get the player id from JWT token after player authentication
	@GetMapping("/{playerId}")
	public Set<TriviaResponse> getAllTriviaByPlayerId(@PathVariable Long playerId) {
		return triviaService.getAllTriviaByPlayerId(playerId);
	}

}
