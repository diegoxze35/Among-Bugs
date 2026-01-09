package org.ipn.mx.among.bugs.service.impl;

import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.RatingRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.RatingResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaRatingsResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.entity.Rating;
import org.ipn.mx.among.bugs.domain.entity.Trivia;
import org.ipn.mx.among.bugs.repository.player.PlayerRepository;
import org.ipn.mx.among.bugs.repository.trivia.RatingRepository;
import org.ipn.mx.among.bugs.repository.trivia.TriviaRepository;
import org.ipn.mx.among.bugs.service.RatingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final TriviaRepository triviaRepository;
    private final PlayerRepository playerRepository;

    @Override
    @Transactional
    public RatingResponse rateTrivia(Long triviaId, RatingRequest request, Long playerId) {
        // Verificar que la trivia existe
        Trivia trivia = triviaRepository.findById(triviaId)
                .orElseThrow(() -> new RuntimeException("Trivia no encontrada"));

        // Verificar que el jugador existe
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        // Verificar si ya existe un rating de este jugador para esta trivia
        Rating rating = ratingRepository.findByPlayerIdAndTriviaId(playerId, triviaId)
                .orElse(null);

        if (rating != null) {
            // Actualizar rating existente
            rating.setScore(request.score());
            rating.setComment(request.comment());
        } else {
            // Crear nuevo rating
            rating = Rating.builder()
                    .player(player)
                    .trivia(trivia)
                    .score(request.score())
                    .comment(request.comment())
                    .build();
        }

        rating = ratingRepository.save(rating);

        return new RatingResponse(
                rating.getId(),
                rating.getPlayer().getId(),
                rating.getPlayer().getUsername(),
                rating.getScore(),
                rating.getComment(),
                rating.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TriviaRatingsResponse getTriviaRatings(Long triviaId) {
        // Verificar que la trivia existe
        Trivia trivia = triviaRepository.findById(triviaId)
                .orElseThrow(() -> new RuntimeException("Trivia no encontrada"));

        // Obtener todos los ratings
        List<Rating> ratings = ratingRepository.findByTriviaIdOrderByCreatedAtDesc(triviaId);

        // Calcular promedio
        Double averageRating = ratingRepository.getAverageRatingByTriviaId(triviaId);
        if (averageRating == null) {
            averageRating = 0.0;
        }

        // Contar total de ratings
        long totalRatings = ratingRepository.countByTriviaId(triviaId);

        // Convertir a DTOs
        List<RatingResponse> ratingResponses = ratings.stream()
                .map(rating -> new RatingResponse(
                        rating.getId(),
                        rating.getPlayer().getId(),
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
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating no encontrado"));

        // Verificar que el jugador es el autor del rating
        if (!rating.getPlayer().getId().equals(playerId)) {
            throw new RuntimeException("Solo puedes eliminar tus propios ratings");
        }

        ratingRepository.delete(rating);
    }
}

