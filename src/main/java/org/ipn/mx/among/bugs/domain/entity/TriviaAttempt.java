package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Table(name = "trivia_attempts")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class TriviaAttempt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(
			targetEntity = Player.class,
			fetch = FetchType.LAZY,
			optional = false
	)
	@JoinColumn(
			name = "player_id",
			referencedColumnName = "id",
			nullable = false
	)
	private Player player;

	@ManyToOne(
			targetEntity = Trivia.class,
			fetch = FetchType.LAZY,
			optional = false
	)
	@JoinColumn(
			name = "trivia_id",
			referencedColumnName = "id",
			nullable = false
	)
	private Trivia trivia;

	@Column(nullable = false)
	private Integer correctAnswers; // Número de respuestas correctas

	@Column(nullable = false)
	private Integer totalQuestions; // Total de preguntas en la trivia

	@Column(nullable = false)
	private Long completionTimeSeconds; // Tiempo total en segundos que tardó en completar

	@Column(nullable = false)
	@CreationTimestamp
	private LocalDateTime attemptDate; // Fecha y hora del intento

	@Builder
	public TriviaAttempt(
			Player player,
			Trivia trivia,
			Integer correctAnswers,
			Integer totalQuestions,
			Long completionTimeSeconds
			//LocalDateTime attemptDate -> Not necessary
	) {
		this.player = player;
		this.trivia = trivia;
		this.correctAnswers = correctAnswers;
		this.totalQuestions = totalQuestions;
		this.completionTimeSeconds = completionTimeSeconds;
		//this.attemptDate = attemptDate != null ? attemptDate : LocalDateTime.now();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof TriviaAttempt other)) return false;
		return id != null && id.equals(other.getId());
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}

