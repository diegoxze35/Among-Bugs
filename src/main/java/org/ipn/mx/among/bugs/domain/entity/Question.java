package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.ipn.mx.among.bugs.domain.entity.json.QuestionOption;

@Table(name = "questions")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 200)
	private String questionText;
	/*
	 * [
	 *   {
	 *     "text": "Option 1",
	 *     "isCorrect": false
	 *   },
	 *   {
	 *     "text": "Option 2",
	 *     "isCorrect": false
	 *   },
	 *   {
	 *     "text": "Option 3", <-- This is the correct answer
	 *     "isCorrect": true
	 *   },
	 *   {
	 *     "text": "Option 4",
	 *     "isCorrect": false
	 *   }
	 * ]
	 * */
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "JSONB", nullable = false)
	private List<QuestionOption> options;
	@ManyToOne(
			targetEntity = Trivia.class,
			optional = false
	)
	@JoinColumn(
			name = "trivia_id",
			referencedColumnName = "id",
			nullable = false
	)
	private Trivia trivia;

	@Builder
	public Question(String questionText, List<QuestionOption> options) {
		this.questionText = questionText;
		this.options = options;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Question other)) return false;
		return id != null && id.equals(other.getId());
	}

	@Override
	public int hashCode() {
		// Retornar una constante asegura que el objeto no "salte" de bucket en el HashSet
		// cuando se guarda en BD y se genera el ID.
		// El rendimiento baja un poco en sets muy grandes, pero garantiza consistencia.
		return getClass().hashCode();
	}

}
