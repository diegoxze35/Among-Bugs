package org.ipn.mx.among.bugs.repository.trivia;

import org.ipn.mx.among.bugs.domain.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * Obtener todos los ratings de una trivia
     */
    List<Rating> findByTriviaIdOrderByCreatedAtDesc(Long triviaId);

    /**
     * Verificar si un jugador ya calificó una trivia
     */
    boolean existsByPlayerIdAndTriviaId(Long playerId, Long triviaId);

    /**
     * Obtener el rating de un jugador para una trivia específica
     */
    Optional<Rating> findByPlayerIdAndTriviaId(Long playerId, Long triviaId);

    /**
     * Calcular el promedio de calificación de una trivia
     */
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.trivia.id = :triviaId")
    Double getAverageRatingByTriviaId(@Param("triviaId") Long triviaId);

    /**
     * Contar cuántas calificaciones tiene una trivia
     */
    long countByTriviaId(Long triviaId);
}

