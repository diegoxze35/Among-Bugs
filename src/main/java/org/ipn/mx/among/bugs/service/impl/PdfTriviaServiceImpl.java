package org.ipn.mx.among.bugs.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.QuestionOptionResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.QuestionResponse;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaWithQuestionsResponse;
import org.ipn.mx.among.bugs.service.PdfTriviaService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PdfTriviaServiceImpl implements PdfTriviaService {

	private final MessageSource messageSource;

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
        final String scoreTitle = messageSource.getMessage("trivia.report.target.score", null, locale);
        String metaText = String.format(scoreTitle + ": %d | " + visibilityTitle + ": %s", trivia.targetScore(), visibility);
        Paragraph meta = new Paragraph(metaText, META_FONT);
        meta.setSpacingAfter(5);
        document.add(meta);
        if (trivia.description() != null && !trivia.description().isBlank()) {
            Paragraph desc = new Paragraph(trivia.description(), NORMAL_FONT);
            desc.setSpacingAfter(10);
            document.add(desc);
        }
		List questionList = new com.lowagie.text.List(com.lowagie.text.List.ORDERED);
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
        com.lowagie.text.List optionsList = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
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

}
