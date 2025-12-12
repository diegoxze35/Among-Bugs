package org.ipn.mx.among.bugs.domain.entity.json;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionOption implements Serializable {
	private String text;
	private Boolean isCorrect;
}
