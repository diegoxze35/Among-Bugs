package org.ipn.mx.among.bugs.repository.trivia;

import java.util.List;
import java.util.Optional;
import org.ipn.mx.among.bugs.domain.entity.TriviaAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TriviaAttemptRepository extends JpaRepository<TriviaAttempt, Long> {

	@Query("""
			SELECT ta FROM TriviaAttempt ta
			WHERE ta.trivia.id = :triviaId
			ORDER BY ta.correctAnswers DESC, ta.completionTimeSeconds ASC
			""")
	List<TriviaAttempt> findTopAttemptsByTriviaId(@Param("triviaId") Long triviaId);

	@Query(value = """
				SELECT ta FROM trivia_attempts ta
				WHERE ta.player_id = :player_id AND ta.trivia_id = :trivia_id
				ORDER BY ta.correct_answers DESC, ta.completion_time_seconds
				FETCH FIRST 1 ROW ONLY
			""",
			nativeQuery = true
	)
	Optional<TriviaAttempt> findTopByPlayerIdAndTriviaId(
			@Param("player_id") Long playerId,
			@Param("trivia_id") Long triviaId
	);

	@Query("""
			SELECT ta FROM TriviaAttempt ta
			JOIN FETCH ta.player
			JOIN FETCH ta.trivia
			ORDER BY ta.correctAnswers DESC, ta.completionTimeSeconds ASC
			""")
	List<TriviaAttempt> findGlobalRanking();

}
