package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.ipn.mx.among.bugs.domain.entity.enums.TrickType;
import org.ipn.mx.among.bugs.domain.entity.json.QuestionOption;

import java.util.List;

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

	@Column(nullable = false)
	private Integer timeLimit = 30; // Tiempo en segundos para responder (20, 40 o 60)

	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private TrickType trickType; // Trampa asignada a esta pregunta (puede ser null)

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
	public Question(String questionText, List<QuestionOption> options, Integer timeLimit, TrickType trickType) {
		this.questionText = questionText;
		this.options = options;
		this.timeLimit = timeLimit != null ? timeLimit : 30;
		this.trickType = trickType;
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
