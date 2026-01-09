package org.ipn.mx.among.bugs.domain.entity.json;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class QuestionOption implements Serializable {
	private String text;
	private Boolean isCorrect;
}
