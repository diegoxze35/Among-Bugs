package org.ipn.mx.among.bugs.service.impl;

import java.util.HashSet;
import java.util.Set;
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
import org.ipn.mx.among.bugs.domain.entity.json.QuestionOption;
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
	@Transactional(rollbackFor = Exception.class)
	public TriviaWithQuestionsResponse createTrivia(Long playerId, CreateTriviaRequest trivia) {
		Player player = playerRepository.findById(playerId).orElseThrow();
		Trivia newTrivia = TriviaMapper.toTriviaEntity(trivia, player);
		newTrivia.setQuestions(new HashSet<>());
		trivia.questions().forEach(q -> newTrivia.addQuestion(TriviaMapper.toQuestionEntity(q)));
		Trivia inserted = triviaRepository.save(newTrivia);
		return TriviaMapper.toDtoWithQuestions(inserted);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public TriviaWithQuestionsResponse updateTrivia(UpdateTriviaRequest trivia, Long triviaId) {
		Trivia triviaToUpdate = triviaRepository.findById(triviaId).orElseThrow();
		triviaToUpdate.setTargetScore(trivia.targetScore());
		triviaToUpdate.setTitle(trivia.title());
		triviaToUpdate.setDescription(trivia.description());
		triviaToUpdate.setIsPublic(trivia.isPublic());
		Set<Question> questionsToRemove = new HashSet<>(triviaToUpdate.getQuestions());
		questionsToRemove.removeIf(q ->
				trivia.questions().stream()
						.anyMatch(qDto -> qDto.id() != null && qDto.id().equals(q.getId()))
		);
		questionsToRemove.forEach(triviaToUpdate::removeQuestion);
		for (UpdateQuestionRequest questionDto : trivia.questions()) {
			if (questionDto.id() == null) {
				Question newQuestion = Question.builder()
						.questionText(questionDto.questionText())
						.options(questionDto.options()
								.stream()
								.map(o -> new QuestionOption(o.text(), o.isCorrect()))
								.toList()
						)
						.build();
				triviaToUpdate.addQuestion(newQuestion);
			} else {
				triviaToUpdate.getQuestions().stream()
						.filter(q -> q.getId().equals(questionDto.id()))
						.findFirst()
						.ifPresent(q -> {
							q.setQuestionText(questionDto.questionText());
							q.setOptions(questionDto.options()
									.stream()
									.map(o -> new QuestionOption(o.text(), o.isCorrect()))
									.toList()
							);
						});
			}
		}
		Trivia savedTrivia = triviaRepository.save(triviaToUpdate);
		return TriviaMapper.toDtoWithQuestions(savedTrivia);
	}

	@Override
	@Transactional(readOnly = true)
	public Set<TriviaResponse> getAllTriviaByPlayerId(Long playerId) {
		Set<Trivia> allTrivia = triviaRepository.findAllByPlayerId((playerId));
		return allTrivia.stream().map(TriviaMapper::toSimpleDto).collect(Collectors.toSet());
	}

	@Override
	@Transactional(readOnly = true)
	public Set<TriviaResponse> getAllPublicTrivia(Pageable pageable) {
		Set<Trivia> allTrivia = triviaRepository.findAllByIsPublic(true);
		return allTrivia.stream().map(TriviaMapper::toSimpleDto).collect(Collectors.toSet());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteTrivia(Long triviaId) {
		triviaRepository.deleteById(triviaId);
	}

}
