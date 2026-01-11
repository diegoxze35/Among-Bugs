package org.ipn.mx.among.bugs.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.RatingRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.RatingResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaRatingsResponse;
import org.ipn.mx.among.bugs.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    /**
     * Crear o actualizar un rating para una trivia
     * POST /api/ratings/trivia/{triviaId}
     */
    @PostMapping("/trivia")
    public ResponseEntity<RatingResponse> rateTrivia(
            @Valid @RequestBody RatingRequest request,
            @AuthenticationPrincipal Long playerId
    ) {
        return ResponseEntity.ok(ratingService.rateTrivia(request, playerId));
    }

    /**
     * Obtener todos los ratings de una trivia
     * GET /api/ratings/trivia/{triviaId}
     */
    @GetMapping("/trivia/{triviaId}")
    public ResponseEntity<TriviaRatingsResponse> getTriviaRatings(
            @PathVariable Long triviaId
    ) {
        return ResponseEntity.ok(ratingService.getTriviaRatings(triviaId));
    }

    /**
     * Eliminar un rating (solo el autor)
     * DELETE /api/ratings/{ratingId}
     */
    @DeleteMapping("/{ratingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRating(
            @PathVariable Long ratingId,
            @AuthenticationPrincipal Long playerId
    ) {
        ratingService.deleteRating(ratingId, playerId);
    }
}

