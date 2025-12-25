package org.ipn.mx.among.bugs.service;

import java.util.Set;
import org.ipn.mx.among.bugs.domain.dto.response.trivia.TriviaWithQuestionsResponse;

public interface PdfTriviaService {
	byte[] generateTriviaReport(Set<TriviaWithQuestionsResponse> trivia);
}
