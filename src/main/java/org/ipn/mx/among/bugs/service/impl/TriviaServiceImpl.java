package org.ipn.mx.among.bugs.service.impl;

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
import org.ipn.mx.among.bugs.mapper.TriviaMapper;
import org.ipn.mx.among.bugs.repository.player.PlayerRepository;
import org.ipn.mx.among.bugs.repository.trivia.TriviaAttemptRepository;
import org.ipn.mx.among.bugs.repository.trivia.TriviaRepository;
import org.ipn.mx.among.bugs.service.TriviaService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TriviaServiceImpl implements TriviaService {

	private final TriviaRepository triviaRepository;
	private final PlayerRepository playerRepository;
	private final TriviaAttemptRepository triviaAttemptRepository;

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
		Trivia trivia = triviaRepository.findById(triviaId)
			.orElseThrow(() -> new RuntimeException("Trivia no encontrada"));
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

	@Override
	@Transactional
	public TriviaAttemptResponse submitAttempt(Long playerId, SubmitTriviaAttemptRequest request) {
		Player player = playerRepository.findById(playerId)
			.orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

		Trivia trivia = triviaRepository.findById(request.triviaId())
			.orElseThrow(() -> new RuntimeException("Trivia no encontrada"));

		// Buscar el mejor intento anterior del jugador para esta trivia
		TriviaAttempt previousBest = triviaAttemptRepository
			.findTopByPlayerIdAndTriviaIdOrderByCorrectAnswersDescCompletionTimeSecondsAsc(
				playerId,
				request.triviaId()
			)
			.orElse(null);

		// Decidir si guardar el nuevo intento
		boolean shouldSave = false;

		if (previousBest == null) {
			// Primera vez que juega esta trivia, siempre se guarda
			shouldSave = true;
		} else {
			// Ya tiene un registro anterior
			int newCorrectAnswers = request.correctAnswers();
			int oldCorrectAnswers = previousBest.getCorrectAnswers();
			long newTime = request.completionTimeSeconds();
			long oldTime = previousBest.getCompletionTimeSeconds();

			// Solo guardar si:
			// 1. Tiene más o igual número de aciertos Y menos tiempo (mejor tiempo)
			// 2. Tiene más aciertos (incluso si el tiempo es peor)
			if (newCorrectAnswers > oldCorrectAnswers) {
				// Tiene más aciertos, siempre se guarda
				shouldSave = true;
			} else if (newCorrectAnswers == oldCorrectAnswers && newTime < oldTime) {
				// Mismo número de aciertos pero mejor tiempo (menor)
				shouldSave = true;
			}
		}

		TriviaAttempt saved = null;

		if (shouldSave) {
			// Eliminar el intento anterior si existe (para mantener solo el mejor)
			if (previousBest != null) {
				triviaAttemptRepository.delete(previousBest);
			}

			// Guardar el nuevo intento
			TriviaAttempt attempt = TriviaAttempt.builder()
				.player(player)
				.trivia(trivia)
				.correctAnswers(request.correctAnswers())
				.totalQuestions(request.totalQuestions())
				.completionTimeSeconds(request.completionTimeSeconds())
				.attemptDate(LocalDateTime.now())
				.build();

			saved = triviaAttemptRepository.save(attempt);
		} else {
			// No se guardó porque no superó el récord anterior
			saved = previousBest;
		}

		return new TriviaAttemptResponse(
			saved.getId(),
			player.getUsername(),
			player.getId(),
			saved.getCorrectAnswers(),
			saved.getTotalQuestions(),
			saved.getCompletionTimeSeconds(),
			saved.getAttemptDate()
		);
	}

	@Override
	@Transactional(readOnly = true)
	public TriviaRankingResponse getTriviaRankings(Long triviaId) {
		Trivia trivia = triviaRepository.findById(triviaId)
			.orElseThrow(() -> new RuntimeException("Trivia no encontrada"));

		List<TriviaAttempt> attempts = triviaAttemptRepository.findTopAttemptsByTriviaId(triviaId);

		List<TriviaAttemptResponse> rankings = attempts.stream()
			.map(attempt -> new TriviaAttemptResponse(
				attempt.getId(),
				attempt.getPlayer().getUsername(),
				attempt.getPlayer().getId(),
				attempt.getCorrectAnswers(),
				attempt.getTotalQuestions(),
				attempt.getCompletionTimeSeconds(),
				attempt.getAttemptDate()
			))
			.collect(Collectors.toList());

		return new TriviaRankingResponse(
			trivia.getId(),
			trivia.getTitle(),
			trivia.getQuestions().size(),
			rankings
		);
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
