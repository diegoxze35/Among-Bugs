package org.ipn.mx.among.bugs.service.impl;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.CreateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.UpdateQuestionRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.UpdateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaWithQuestionsResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.entity.Question;
import org.ipn.mx.among.bugs.domain.entity.Trivia;
import org.ipn.mx.among.bugs.mapper.TriviaMapper;
import org.ipn.mx.among.bugs.repository.player.PlayerRepository;
import org.ipn.mx.among.bugs.repository.trivia.TriviaRepository;
import org.ipn.mx.among.bugs.service.TriviaService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TriviaServiceImpl implements TriviaService {

	private final TriviaRepository triviaRepository;
	private final PlayerRepository playerRepository;

	@Override
	@Transactional(readOnly = true)
	public Set<TriviaResponse> getAllTriviaByPlayerId(Long playerId) {
		Set<Trivia> allTrivia = triviaRepository.findAllByPlayerId((playerId));
		return allTrivia.stream().map(TriviaMapper::toSimpleDto).collect(Collectors.toSet());
	}

	@Override
	@Transactional(readOnly = true)
	public Set<TriviaResponse> getAllPublicTrivia(Pageable pageable) {
		Set<Trivia> allTrivia = triviaRepository.findAllByIsPublicTrue();
		return allTrivia.stream().map(TriviaMapper::toSimpleDto).collect(Collectors.toSet());
	}

	@Override
	@Transactional(readOnly = true)
	public Set<TriviaWithQuestionsResponse> getAllTriviaWithQuestionsByPlayerId(Long playerId) {
		Set<Trivia> allTrivia = triviaRepository.findAllByPlayerId(playerId);
		return allTrivia.stream().map(TriviaMapper::toDtoWithQuestions).collect(Collectors.toSet());
	}

	@Override
	@Transactional
	public TriviaWithQuestionsResponse createTrivia(Long playerId, CreateTriviaRequest request) {
		Player playerRef = playerRepository.getReferenceById(playerId);
		Trivia newTrivia = TriviaMapper.toTriviaEntity(request, playerRef);
		request.questions().forEach(q -> newTrivia.addQuestion(TriviaMapper.toQuestionEntity(q)));
		Trivia inserted = triviaRepository.save(newTrivia);
		return TriviaMapper.toDtoWithQuestions(inserted);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public TriviaWithQuestionsResponse updateTrivia(UpdateTriviaRequest request) {
		final Long triviaId = request.id();
		Trivia trivia = triviaRepository.getReferenceById(triviaId);
		TriviaMapper.updateTriviaFromDto(trivia, request);
		syncQuestions(trivia, request.questions());
		return TriviaMapper.toDtoWithQuestions(triviaRepository.save(trivia));
	}

	@Override
	@Transactional
	public void deleteTrivia(Long triviaId) {
		triviaRepository.deleteById(triviaId);
	}

	private void syncQuestions(Trivia trivia, Set<UpdateQuestionRequest> incomingQuestions) {
		Map<Long, UpdateQuestionRequest> incomingMap = incomingQuestions.stream()
				.filter(dto -> dto.id() != null)
				.collect(Collectors.toMap(UpdateQuestionRequest::id, Function.identity()));
		trivia.getQuestions().removeIf(existingQuestion ->
				!incomingMap.containsKey(existingQuestion.getId()));
		for (UpdateQuestionRequest dto : incomingQuestions) {
			if (dto.id() == null) {
				Question newQuestion = TriviaMapper.createQuestionFromDto(dto);
				trivia.addQuestion(newQuestion);
			} else {
				trivia.getQuestions().stream()
						.filter(q -> q.getId().equals(dto.id()))
						.findFirst()
						.ifPresent(existingQuestion ->
								TriviaMapper.updateQuestionFromDto(existingQuestion, dto)
						);
			}
		}
	}

}
