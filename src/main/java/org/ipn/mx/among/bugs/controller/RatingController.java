package org.ipn.mx.among.bugs.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.RatingRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.RatingResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaRatingsResponse;
import org.ipn.mx.among.bugs.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    /**
     * Crear o actualizar un rating para una trivia
     * POST /api/ratings/trivia/{triviaId}
     */
    @PostMapping("/trivia/{triviaId}")
    public ResponseEntity<RatingResponse> rateTrivia(
            @PathVariable Long triviaId,
            @Valid @RequestBody RatingRequest request,
            @AuthenticationPrincipal Long playerId
    ) {
        return ResponseEntity.ok(ratingService.rateTrivia(triviaId, request, playerId));
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
    public ResponseEntity<String> deleteRating(
            @PathVariable Long ratingId,
            @AuthenticationPrincipal Long playerId
    ) {
        ratingService.deleteRating(ratingId, playerId);
        return ResponseEntity.ok("Rating eliminado exitosamente");
    }
}

