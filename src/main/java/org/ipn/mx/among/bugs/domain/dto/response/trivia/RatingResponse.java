package org.ipn.mx.among.bugs.domain.dto.response.trivia;

import java.time.LocalDateTime;

/**
 * Response para un rating individual
 */
public record RatingResponse(
        Long id,
        String playerUsername,
        Short score,
        String comment,
        LocalDateTime createdAt
) {
}

