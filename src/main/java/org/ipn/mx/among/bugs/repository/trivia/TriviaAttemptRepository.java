package org.ipn.mx.among.bugs.repository.trivia;

import org.ipn.mx.among.bugs.domain.entity.TriviaAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TriviaAttemptRepository extends JpaRepository<TriviaAttempt, Long> {

	// Obtener los mejores intentos de una trivia ordenados por tiempo (más rápidos primero)
	@Query("""
		SELECT ta FROM TriviaAttempt ta
		WHERE ta.trivia.id = :triviaId
		ORDER BY ta.correctAnswers DESC, ta.completionTimeSeconds ASC
		""")
	List<TriviaAttempt> findTopAttemptsByTriviaId(@Param("triviaId") Long triviaId);

	// Obtener todos los intentos de un jugador en una trivia específica
	@Query("""
		SELECT ta FROM TriviaAttempt ta
		WHERE ta.trivia.id = :triviaId AND ta.player.id = :playerId
		ORDER BY ta.attemptDate DESC
		""")
	List<TriviaAttempt> findAttemptsByTriviaAndPlayer(
		@Param("triviaId") Long triviaId,
		@Param("playerId") Long playerId
	);

	// Obtener el mejor intento de un jugador en una trivia
	@Query("""
		SELECT ta FROM TriviaAttempt ta
		WHERE ta.trivia.id = :triviaId AND ta.player.id = :playerId
		ORDER BY ta.correctAnswers DESC, ta.completionTimeSeconds ASC
		LIMIT 1
		""")
	TriviaAttempt findBestAttemptByTriviaAndPlayer(
		@Param("triviaId") Long triviaId,
		@Param("playerId") Long playerId
	);

	// Método usando Spring Data JPA para encontrar el mejor intento anterior
	java.util.Optional<TriviaAttempt> findTopByPlayerIdAndTriviaIdOrderByCorrectAnswersDescCompletionTimeSecondsAsc(
		Long playerId,
		Long triviaId
	);

	// Obtener el ranking global de todos los jugadores
	@Query("""
		SELECT ta FROM TriviaAttempt ta
		JOIN FETCH ta.player
		JOIN FETCH ta.trivia
		ORDER BY ta.correctAnswers DESC, ta.completionTimeSeconds ASC
		""")
	List<TriviaAttempt> findGlobalRanking();

}
