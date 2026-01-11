package org.ipn.mx.among.bugs.service;

import org.ipn.mx.among.bugs.domain.dto.request.trivia.RatingRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.RatingResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaRatingsResponse;

public interface RatingService {

    /**
     * Crear o actualizar un rating para una trivia
     */
    RatingResponse rateTrivia(RatingRequest request, Long playerId);

    /**
     * Obtener todos los ratings de una trivia
     */
    TriviaRatingsResponse getTriviaRatings(Long triviaId);

    /**
     * Eliminar un rating (solo el autor)
     */
    void deleteRating(Long ratingId, Long playerId);
}
