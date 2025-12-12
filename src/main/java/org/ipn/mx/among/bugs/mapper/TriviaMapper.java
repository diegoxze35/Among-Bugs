package org.ipn.mx.among.bugs.mapper;

import java.util.stream.Collectors;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.CreateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.QuestionRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.QuestionOptionResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.QuestionResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaWithQuestionsResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.entity.Question;
import org.ipn.mx.among.bugs.domain.entity.Trivia;
import org.ipn.mx.among.bugs.domain.entity.json.QuestionOption;

public class TriviaMapper {

	public static TriviaResponse toSimpleDto(Trivia trivia) {
		return new TriviaResponse(
				trivia.getId(),
				trivia.getTargetScore(),
				trivia.getTitle(),
				trivia.getDescription(),
				trivia.getIsPublic()
		);
	}

	public static TriviaWithQuestionsResponse toDtoWithQuestions(Trivia trivia) {
		return new TriviaWithQuestionsResponse(
				trivia.getId(),
				trivia.getTargetScore(),
				trivia.getTitle(),
				trivia.getDescription(),
				trivia.getIsPublic(),
				trivia.getQuestions().stream().map(TriviaMapper::toQuestionDto).toList()
		);
	}

	private static QuestionResponse toQuestionDto(Question question) {
		return new QuestionResponse(
				question.getId(),
				question.getQuestionText(),
				question.getOptions().stream().map(TriviaMapper::toOptionsDto).toList()
		);
	}

	private static QuestionOptionResponse toOptionsDto(QuestionOption option) {
		return new QuestionOptionResponse(option.getText(), option.getIsCorrect());
	}

	public static Question toQuestionEntity(QuestionRequest request) {
		return Question.builder()
				.questionText(request.questionText())
				.options(request.options().stream().map(o ->
						new QuestionOption(o.text(), o.isCorrect())
				).toList())
				.build();
	}

	public static Trivia toTriviaEntity(CreateTriviaRequest request, Player player) {
		return Trivia.builder()
				.player(player)
				.targetScore(request.targetScore())
				.description(request.description())
				.isPublic(request.isPublic())
				.title(request.title())
				/*.questions(
						request
								.questions()
								.stream()
								.map(TriviaMapper::toQuestionEntity)
								.collect(Collectors.toSet())
				)*/
				.build();
	}

}
