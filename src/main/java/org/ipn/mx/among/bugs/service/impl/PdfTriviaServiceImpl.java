package org.ipn.mx.among.bugs.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.List;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.QuestionOptionResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.QuestionResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaWithQuestionsResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.entity.TriviaAttempt;
import org.ipn.mx.among.bugs.repository.player.PlayerRepository;
import org.ipn.mx.among.bugs.repository.trivia.TriviaAttemptRepository;
import org.ipn.mx.among.bugs.service.PdfTriviaService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PdfTriviaServiceImpl implements PdfTriviaService {

	private final MessageSource messageSource;
	private final PlayerRepository playerRepository;
	private final TriviaAttemptRepository triviaAttemptRepository;

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.DARK_GRAY);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
    private static final Font META_FONT = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, Color.GRAY);
    private static final Font CORRECT_OPTION_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(0, 128, 0));
    private static final Font WRONG_OPTION_FONT = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);

	@Override
	public byte[] generateTriviaReport(Set<TriviaWithQuestionsResponse> trivia) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();
            Locale locale = LocaleContextHolder.getLocale();
            document.addTitle(messageSource.getMessage("trivia.report.title", null, locale));
            //document.addAuthor("Among Bugs System");
            Paragraph mainTitle = new Paragraph(messageSource.getMessage("trivia.report.main.title", null, locale), TITLE_FONT);
            mainTitle.setAlignment(Element.ALIGN_CENTER);
            mainTitle.setSpacingAfter(20);
            document.add(mainTitle);
            for (TriviaWithQuestionsResponse t : trivia) {
                addTriviaSection(document, t);
            }
            document.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Error", e);
        }
	}

	private void addTriviaSection(Document document, TriviaWithQuestionsResponse trivia) throws DocumentException {
        Locale locale = LocaleContextHolder.getLocale();
        Paragraph triviaTitle = new Paragraph(trivia.title(), SUBTITLE_FONT);
        triviaTitle.setSpacingBefore(10);
        document.add(triviaTitle);
        final String visibilityKey = Boolean.TRUE.equals(trivia.isPublic()) ? "trivia.report.visibility.public" : "trivia.report.visibility.private";
        final String visibility = messageSource.getMessage(visibilityKey, null, locale);
        final String visibilityTitle = messageSource.getMessage("trivia.report.visibility", null, locale);
        String metaText = String.format("%s: %s | Total preguntas: %d", visibilityTitle, visibility, trivia.questions().size());
        Paragraph meta = new Paragraph(metaText, META_FONT);
        meta.setSpacingAfter(5);
        document.add(meta);
        if (trivia.description() != null && !trivia.description().isBlank()) {
            Paragraph desc = new Paragraph(trivia.description(), NORMAL_FONT);
            desc.setSpacingAfter(10);
            document.add(desc);
        }
		List questionList = new List(List.ORDERED);
        questionList.setFirst(1);
        questionList.setIndentationLeft(20);
        if (trivia.questions() != null) {
            for (QuestionResponse question : trivia.questions()) {
                ListItem questionItem = new ListItem(question.questionText(), NORMAL_FONT);
                questionItem.setSpacingAfter(5);
                addOptionsToQuestion(questionItem, question);

                questionList.add(questionItem);
            }
        }
        document.add(questionList);
        LineSeparator separator = new LineSeparator();
        separator.setLineColor(Color.LIGHT_GRAY);
        separator.setOffset(-5);
        document.add(new Paragraph(" ")); // Espacio vacío
        document.add(separator);
        document.add(new Paragraph(" ")); // Espacio vacío
    }

	private void addOptionsToQuestion(ListItem questionItem, QuestionResponse question) {
        List optionsList = new List(List.UNORDERED);
        optionsList.setListSymbol("•");
        optionsList.setIndentationLeft(20);
        if (question.options() != null) {
            for (QuestionOptionResponse option : question.options()) {
                Font optionFont = Boolean.TRUE.equals(option.isCorrect()) ? CORRECT_OPTION_FONT : WRONG_OPTION_FONT;
                String optionText = option.text();
                if (Boolean.TRUE.equals(option.isCorrect())) {
                    Locale locale = LocaleContextHolder.getLocale();
                    optionText += " ("+ messageSource.getMessage("trivia.report.option.correct", null, locale) +")";
                }
                ListItem optionItem = new ListItem(optionText, optionFont);
                optionsList.add(optionItem);
            }
        }
        questionItem.add(optionsList);
    }

	@Override
	public byte[] generatePlayerStatsReport(Long playerId) {
		try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Player player = playerRepository.findById(playerId)
				.orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

			// Obtener todos los intentos del jugador (solo los mejores)
			java.util.List<TriviaAttempt> attempts = triviaAttemptRepository.findAll().stream()
				.filter(attempt -> attempt.getPlayer().getId().equals(playerId))
				.toList();

			if (attempts.isEmpty()) {
				throw new RuntimeException("No hay estadísticas disponibles para este jugador");
			}

			Document document = new Document(PageSize.A4);
			PdfWriter.getInstance(document, out);
			document.open();

			// Título del reporte
			Paragraph mainTitle = new Paragraph("ESTADÍSTICAS DEL JUGADOR", TITLE_FONT);
			mainTitle.setAlignment(Element.ALIGN_CENTER);
			mainTitle.setSpacingAfter(10);
			document.add(mainTitle);

			// Nombre del jugador
			Paragraph playerName = new Paragraph("Jugador: " + player.getUsername(), SUBTITLE_FONT);
			playerName.setAlignment(Element.ALIGN_CENTER);
			playerName.setSpacingAfter(20);
			document.add(playerName);

			// Resumen de estadísticas
			Paragraph summary = new Paragraph("Resumen de Mejores Puntuaciones", SUBTITLE_FONT);
			summary.setSpacingBefore(10);
			summary.setSpacingAfter(10);
			document.add(summary);

			// Crear tabla de estadísticas
			PdfPTable table = new PdfPTable(5);
			table.setWidthPercentage(100);
			table.setSpacingBefore(10);
			table.setSpacingAfter(10);

			// Encabezados de la tabla
			addTableHeader(table, "Trivia");
			addTableHeader(table, "Aciertos");
			addTableHeader(table, "Total");
			addTableHeader(table, "Tiempo");
			addTableHeader(table, "Fecha");

			// Agregar filas de datos
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
			for (TriviaAttempt attempt : attempts) {
				addTableCell(table, attempt.getTrivia().getTitle());
				addTableCell(table, String.valueOf(attempt.getCorrectAnswers()));
				addTableCell(table, String.valueOf(attempt.getTotalQuestions()));
				addTableCell(table, formatTime(attempt.getCompletionTimeSeconds()));
				addTableCell(table, attempt.getAttemptDate().format(formatter));
			}

			document.add(table);

			// Estadísticas generales
			int totalAttempts = attempts.size();
			int totalCorrect = attempts.stream().mapToInt(TriviaAttempt::getCorrectAnswers).sum();
			int totalQuestions = attempts.stream().mapToInt(TriviaAttempt::getTotalQuestions).sum();
			double avgPercentage = totalQuestions > 0 ? (totalCorrect * 100.0 / totalQuestions) : 0;

			LineSeparator separator = new LineSeparator();
			separator.setLineColor(Color.LIGHT_GRAY);
			document.add(new Paragraph(" "));
			document.add(separator);
			document.add(new Paragraph(" "));

			Paragraph statsTitle = new Paragraph("Estadísticas Globales", SUBTITLE_FONT);
			statsTitle.setSpacingBefore(10);
			statsTitle.setSpacingAfter(10);
			document.add(statsTitle);

			document.add(new Paragraph("Total de trivias completadas: " + totalAttempts, NORMAL_FONT));
			document.add(new Paragraph("Total de respuestas correctas: " + totalCorrect + " / " + totalQuestions, NORMAL_FONT));
			document.add(new Paragraph("Porcentaje promedio de aciertos: " + String.format("%.2f", avgPercentage) + "%", NORMAL_FONT));

			document.close();
			return out.toByteArray();
		} catch (DocumentException | IOException e) {
			throw new RuntimeException("Error al generar el reporte de estadísticas", e);
		}
	}

	private void addTableHeader(PdfPTable table, String headerTitle) {
		PdfPCell header = new PdfPCell();
		header.setBackgroundColor(new Color(63, 81, 181));
		header.setBorderWidth(1);
		header.setPhrase(new Phrase(headerTitle, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE)));
		header.setHorizontalAlignment(Element.ALIGN_CENTER);
		header.setPadding(5);
		table.addCell(header);
	}

	private void addTableCell(PdfPTable table, String text) {
		PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL_FONT));
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPadding(5);
		table.addCell(cell);
	}

	private String formatTime(long seconds) {
		int minutes = (int) (seconds / 60);
		int remainingSeconds = (int) (seconds % 60);
		return String.format("%d:%02d", minutes, remainingSeconds);
	}

}
