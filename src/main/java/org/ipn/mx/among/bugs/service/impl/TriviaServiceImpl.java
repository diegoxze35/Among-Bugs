package org.ipn.mx.among.bugs.service.impl;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

	@Override
	@Transactional(readOnly = true)
	public byte[] createPdfReport(Pageable pageable) {
		var allTrivia = this.getAllPublicTrivia(pageable);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, out);
			document.open();
			Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLUE);
			Paragraph title = new Paragraph("Trivia Report", fontTitle);
			title.setAlignment(Element.ALIGN_CENTER);
			document.add(title);
			document.add(Chunk.NEWLINE);

			PdfPTable table = new PdfPTable(5);
			table.setWidthPercentage(100);
			table.setWidths(new float[]{2f, 2f, 3f, 4f, 1.5f});

			addTableHeader(table);

			for (var t : allTrivia) {
				table.addCell(String.valueOf(t.id()));
				table.addCell(String.valueOf(t.targetScore()));
				table.addCell(t.title());
				table.addCell(t.description());
				table.addCell(t.isPublic() ? "Yes" : "No");
			}

			document.add(table);
			document.close();

			return out.toByteArray();

		} catch (DocumentException | IOException e) {
			throw new RuntimeException("Error creating PDF", e);
		}
	}

	private void addTableHeader(PdfPTable table) {
		String[] headers = {"ID", "Score", "Title", "Description", "Public"};

		for (String header : headers) {
			PdfPCell headerCell = new PdfPCell();
			headerCell.setBackgroundColor(Color.LIGHT_GRAY);
			headerCell.setPhrase(new Phrase(header));
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(headerCell);
		}
	}

}
