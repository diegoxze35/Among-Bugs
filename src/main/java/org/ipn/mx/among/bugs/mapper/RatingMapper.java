package org.ipn.mx.among.bugs.mapper;

import org.ipn.mx.among.bugs.domain.dto.request.trivia.RatingRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.RatingResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.entity.Rating;
import org.ipn.mx.among.bugs.domain.entity.Trivia;

public class RatingMapper {
	private RatingMapper() {
	}

	public static RatingResponse toDto(Rating rating) {
		return new RatingResponse(
				rating.getId(),
				rating.getPlayer().getUsername(),
				rating.getScore(),
				rating.getComment(),
				rating.getCreatedAt()
		);
	}

	public static Rating toEntity(RatingRequest request, Player player, Trivia trivia) {
		return Rating.builder()
				.player(player)
				.trivia(trivia)
				.score(request.score())
				.comment(request.comment())
				.build();
	}
}
