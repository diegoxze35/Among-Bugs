package org.ipn.mx.among.bugs.repository.trivia;

import org.ipn.mx.among.bugs.domain.entity.Trivia;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface TriviaRepository extends JpaRepository<Trivia, Long> {

	Set<Trivia> findAllByPlayerId(Long playerId);

	//@EntityGraph(attributePaths = {"player", "questions"}) ?????
	Set<Trivia> findAllByIsPublicTrue();

}
