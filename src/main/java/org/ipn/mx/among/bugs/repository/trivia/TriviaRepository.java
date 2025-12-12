package org.ipn.mx.among.bugs.repository.trivia;

import java.util.Set;
import org.ipn.mx.among.bugs.domain.entity.Trivia;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TriviaRepository extends PagingAndSortingRepository<Trivia, Long>, CrudRepository<Trivia, Long> {

	Set<Trivia> findAllByPlayerId(Long playerId);

	Set<Trivia> findAllByIsPublic(Boolean isPublic);

}
