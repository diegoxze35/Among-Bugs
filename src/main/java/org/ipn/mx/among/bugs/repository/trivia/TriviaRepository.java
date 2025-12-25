package org.ipn.mx.among.bugs.repository.trivia;

import java.util.Set;
import org.ipn.mx.among.bugs.domain.entity.Trivia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TriviaRepository extends JpaRepository<Trivia, Long> {

	Set<Trivia> findAllByPlayerId(Long playerId);
	Set<Trivia> findAllByIsPublicTrue();

}
