package org.ipn.mx.among.bugs.repository.rating;

import java.util.List;
import java.util.Optional;
import org.ipn.mx.among.bugs.domain.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

    @Modifying
    @Query("DELETE FROM Rating r WHERE r.id = :id AND r.player.id = :playerId")
    void deleteByIdAndPlayerId(@Param("id") Long id, @Param("playerId") Long playerId);
}
