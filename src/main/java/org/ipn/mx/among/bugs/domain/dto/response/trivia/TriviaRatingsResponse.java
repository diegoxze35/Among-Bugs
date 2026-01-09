package org.ipn.mx.among.bugs.domain.dto.response.trivia;

import java.util.List;

/**
 * Response con todos los ratings de una trivia
 */
public record TriviaRatingsResponse(
        Long triviaId,
        String triviaTitle,
        Double averageRating,
        Long totalRatings,
        List<RatingResponse> ratings
) {
}

