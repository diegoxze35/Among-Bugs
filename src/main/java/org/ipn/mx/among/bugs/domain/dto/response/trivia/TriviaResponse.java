package org.ipn.mx.among.bugs.domain.dto.response.trivia;

public record TriviaResponse(
        Long id,
        String title,
        String description,
        Boolean isPublic,
        String creatorUsername,
        Integer totalQuestions
) {
}
