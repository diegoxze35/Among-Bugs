package org.ipn.mx.among.bugs.mapper;

import org.ipn.mx.among.bugs.domain.dto.request.trivia.CreateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.QuestionRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.UpdateQuestionRequest;
import org.ipn.mx.among.bugs.domain.dto.request.trivia.UpdateTriviaRequest;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.QuestionOptionResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.QuestionResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaWithQuestionsResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.entity.Question;
import org.ipn.mx.among.bugs.domain.entity.Trivia;
import org.ipn.mx.among.bugs.domain.entity.json.QuestionOption;

public class TriviaMapper {

	private TriviaMapper() {}

	public static Trivia toTriviaEntity(CreateTriviaRequest request, Player player) {
		return Trivia.builder()
				.player(player)
				.description(request.description())
				.isPublic(request.isPublic())
				.title(request.title())
				.build();
	}

	public static Question toQuestionEntity(QuestionRequest request) {
		return Question.builder()
				.questionText(request.questionText())
				.options(request.options().stream().map(o ->
						new QuestionOption(o.text(), o.isCorrect())
				).toList())
				.timeLimit(request.timeLimit())
				.trickType(request.trickType())
				.build();
	}

	public static TriviaResponse toSimpleDto(Trivia trivia) {
        return new TriviaResponse(
                trivia.getId(),
                trivia.getTitle(),
                trivia.getDescription(),
                trivia.getIsPublic(),
                trivia.getPlayer().getUsername(),
                trivia.getQuestions().size()
        );
	}

	public static TriviaWithQuestionsResponse toDtoWithQuestions(Trivia trivia) {
		return new TriviaWithQuestionsResponse(
				trivia.getId(),
				trivia.getTitle(),
				trivia.getDescription(),
				trivia.getIsPublic(),
				trivia.getPlayer().getUsername(),
				trivia.getQuestions().stream().map(TriviaMapper::toQuestionDto).toList()
		);
	}

	public static void updateTriviaFromDto(Trivia trivia, UpdateTriviaRequest request) {
		trivia.setTitle(request.title());
		trivia.setDescription(request.description());
		trivia.setIsPublic(request.isPublic());
	}

	public static Question createQuestionFromDto(UpdateQuestionRequest request) {
		return Question.builder()
				.questionText(request.questionText())
				.options(request.options().stream()
						.map(o -> new QuestionOption(o.text(), o.isCorrect()))
						.toList())
				.timeLimit(request.timeLimit())
				.trickType(request.trickType())
				.build();
	}

	public static void updateQuestionFromDto(Question question, UpdateQuestionRequest request) {
		question.setQuestionText(request.questionText());
		question.setOptions(request.options().stream()
				.map(o -> new QuestionOption(o.text(), o.isCorrect()))
				.toList());
		question.setTimeLimit(request.timeLimit());
		question.setTrickType(request.trickType());
	}

	private static QuestionResponse toQuestionDto(Question question) {
		return new QuestionResponse(
				question.getId(),
				question.getQuestionText(),
				question.getOptions().stream().map(TriviaMapper::toOptionsDto).toList(),
				question.getTimeLimit(),
				question.getTrickType()
		);
	}

	private static QuestionOptionResponse toOptionsDto(QuestionOption option) {
		return new QuestionOptionResponse(option.getText(), option.getIsCorrect());
	}

}
