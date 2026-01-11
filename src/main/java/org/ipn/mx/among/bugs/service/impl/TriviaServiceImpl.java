package org.ipn.mx.among.bugs.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.CreateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.SubmitTriviaAttemptRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.UpdateQuestionRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.UpdateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaAttemptResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaRankingResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaWithQuestionsResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.entity.Question;
import org.ipn.mx.among.bugs.domain.entity.Trivia;
import org.ipn.mx.among.bugs.domain.entity.TriviaAttempt;
import org.ipn.mx.among.bugs.exception.ResourceNotFoundException;
import org.ipn.mx.among.bugs.mapper.TriviaAttemptMapper;
import org.ipn.mx.among.bugs.mapper.TriviaMapper;
import org.ipn.mx.among.bugs.repository.player.PlayerRepository;
import org.ipn.mx.among.bugs.repository.trivia.TriviaAttemptRepository;
import org.ipn.mx.among.bugs.repository.trivia.TriviaRepository;
import org.ipn.mx.among.bugs.service.TriviaService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TriviaServiceImpl implements TriviaService {

	private final TriviaRepository triviaRepository;
	private final PlayerRepository playerRepository;
	private final TriviaAttemptRepository triviaAttemptRepository;
	private final MessageSource messageSource;

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
	@Transactional(readOnly = true)
	public TriviaWithQuestionsResponse getTriviaWithQuestions(Long triviaId) {
		Trivia trivia = triviaRepository.findById(triviaId).orElseThrow(() -> {
			Locale locale = LocaleContextHolder.getLocale();
			final String message = messageSource.getMessage("trivia.not.found", null, locale);
			return new ResourceNotFoundException(message);
		});
		return TriviaMapper.toDtoWithQuestions(trivia);
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

	/*I refactored this method to not "overquerying" the database -> Diego*/
	@Override
	@Transactional
	public TriviaAttemptResponse submitAttempt(Long playerId, SubmitTriviaAttemptRequest request) {
		if (!triviaRepository.existsById(request.triviaId())) {
			Locale locale = LocaleContextHolder.getLocale();
			final String message = messageSource.getMessage("trivia.not.found", null, locale);
			throw new ResourceNotFoundException(message);
		}
		Player playerRef = playerRepository.getReferenceById(playerId); // Lazy fetching
		Trivia triviaRef = triviaRepository.getReferenceById(request.triviaId()); // Lazy fetching
		Optional<TriviaAttempt> previousBestOptional = triviaAttemptRepository.findTopByPlayerIdAndTriviaId(playerId, request.triviaId());
		if (previousBestOptional.isEmpty()) {
			TriviaAttempt newAttempt = TriviaAttemptMapper.toEntity(request, playerRef, triviaRef);
			return TriviaAttemptMapper.toDto(triviaAttemptRepository.save(newAttempt));
		} else {
			TriviaAttempt previousBest = previousBestOptional.get();
			int newCorrectAnswers = request.correctAnswers();
			int oldCorrectAnswers = previousBest.getCorrectAnswers();
			long newTime = request.completionTimeSeconds();
			long oldTime = previousBest.getCompletionTimeSeconds();
			if (newCorrectAnswers > oldCorrectAnswers || (newCorrectAnswers == oldCorrectAnswers && newTime < oldTime)) {
				previousBest.setCorrectAnswers(newCorrectAnswers);
				previousBest.setCompletionTimeSeconds(newTime);
				return TriviaAttemptMapper.toDto(triviaAttemptRepository.save(previousBest));
			}
			return TriviaAttemptMapper.toDto(previousBest);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public TriviaRankingResponse getTriviaRankings(Long triviaId) {
		Trivia trivia = triviaRepository.findById(triviaId).orElseThrow(() -> {
			Locale locale = LocaleContextHolder.getLocale();
			final String message = messageSource.getMessage("trivia.not.found", null, locale);
			return new ResourceNotFoundException(message);
		});
		List<TriviaAttempt> attempts = triviaAttemptRepository.findTopAttemptsByTriviaId(triviaId);
		List<TriviaAttemptResponse> rankings = attempts
				.stream()
				.map(TriviaAttemptMapper::toDto)
				.collect(Collectors.toList());
		return new TriviaRankingResponse(trivia.getId(), trivia.getTitle(), trivia.getQuestions().size(), rankings);
	}

	private void syncQuestions(Trivia trivia, Set<UpdateQuestionRequest> incomingQuestions) {
		Map<Long, UpdateQuestionRequest> incomingMap = incomingQuestions
				.stream()
				.filter(dto -> dto.id() != null)
				.collect(Collectors.toMap(UpdateQuestionRequest::id, Function.identity()));
		trivia.getQuestions().removeIf(existingQuestion -> !incomingMap.containsKey(existingQuestion.getId()));
		for (UpdateQuestionRequest dto : incomingQuestions) {
			if (dto.id() == null) {
				Question newQuestion = TriviaMapper.createQuestionFromDto(dto);
				trivia.addQuestion(newQuestion);
			} else {
				trivia.getQuestions()
						.stream()
						.filter(q -> q.getId().equals(dto.id()))
						.findFirst()
						.ifPresent(existingQuestion -> TriviaMapper.updateQuestionFromDto(existingQuestion, dto));
			}
		}
	}

}
