package org.ipn.mx.among.bugs.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.RatingRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.RatingResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaRatingsResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.entity.Rating;
import org.ipn.mx.among.bugs.domain.entity.Trivia;
import org.ipn.mx.among.bugs.exception.ResourceNotFoundException;
import org.ipn.mx.among.bugs.mapper.RatingMapper;
import org.ipn.mx.among.bugs.repository.player.PlayerRepository;
import org.ipn.mx.among.bugs.repository.rating.RatingRepository;
import org.ipn.mx.among.bugs.repository.trivia.TriviaRepository;
import org.ipn.mx.among.bugs.service.RatingService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

	private final RatingRepository ratingRepository;
	private final TriviaRepository triviaRepository;
	private final PlayerRepository playerRepository;
	private final MessageSource messageSource;

	@Override
	@Transactional
	public RatingResponse rateTrivia(RatingRequest request, Long playerId) {
		final long triviaId = request.triviaId();
		if (!triviaRepository.existsById(triviaId)) {
			Locale locale = LocaleContextHolder.getLocale();
			final String message = messageSource.getMessage("trivia.not.found", null, locale);
			throw new ResourceNotFoundException(message);
		}
		Player player = playerRepository.getReferenceById(playerId);
		Trivia trivia = triviaRepository.getReferenceById(triviaId);
		Optional<Rating> optionalRating = ratingRepository.findByPlayerIdAndTriviaId(playerId, triviaId);
		Rating rating;

		if (optionalRating.isPresent()) {
			rating = optionalRating.get();
			rating.setScore(request.score());
			rating.setComment(request.comment());
		} else {
			rating = RatingMapper.toEntity(request, player, trivia);
		}

		return RatingMapper.toDto(ratingRepository.save(rating));
	}

	@Override
	@Transactional(readOnly = true)
	public TriviaRatingsResponse getTriviaRatings(Long triviaId) {
		Trivia trivia = triviaRepository.findById(triviaId).orElseThrow(() -> {
			Locale locale = LocaleContextHolder.getLocale();
			final String message = messageSource.getMessage("trivia.not.found", null, locale);
			return new ResourceNotFoundException(message);
		});
		List<Rating> ratings = ratingRepository.findByTriviaIdOrderByCreatedAtDesc(triviaId);
		Double averageRating = ratingRepository.getAverageRatingByTriviaId(triviaId);
		if (averageRating == null) {
			averageRating = 0.0;
		}
		long totalRatings = ratingRepository.countByTriviaId(triviaId);
		List<RatingResponse> ratingResponses = ratings.stream()
				.map(rating -> new RatingResponse(
						rating.getId(),
						rating.getPlayer().getUsername(),
						rating.getScore(),
						rating.getComment(),
						rating.getCreatedAt()
				))
				.collect(Collectors.toList());

		return new TriviaRatingsResponse(
				trivia.getId(),
				trivia.getTitle(),
				averageRating,
				totalRatings,
				ratingResponses
		);
	}

	@Override
	@Transactional
	public void deleteRating(Long ratingId, Long playerId) {
		ratingRepository.deleteByIdAndPlayerId(ratingId, playerId);
	}
}
