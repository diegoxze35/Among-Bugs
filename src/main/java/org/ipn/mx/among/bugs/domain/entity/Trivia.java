package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "trivia")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Trivia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(
			targetEntity = Player.class,
			fetch = FetchType.LAZY,
			optional = false
	)
	@JoinColumn(
			name = "creator_player_id",
			referencedColumnName = "id",
			nullable = false
	)
	private Player player;
	@Column(nullable = false)
	private Integer targetScore;
	@Column(nullable = false, length = 50)
	private String title;
	@Column(length = 200)
	private String description;
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
	protected Boolean isPublic;
	@OneToMany(
			targetEntity = Question.class,
			mappedBy = "trivia",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.ALL},
			orphanRemoval = true
	)
	private Set<Question> questions;

	@Builder
	public Trivia(
			Player player,
			Integer targetScore,
			String title,
			String description,
			boolean isPublic
	) {
		this.player = player;
		this.targetScore = targetScore;
		this.title = title;
		this.description = description;
		this.isPublic = isPublic;
		this.questions = new HashSet<>();
	}

	public void addQuestion(Question question) {
        questions.add(question);
        question.setTrivia(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setTrivia(null);
    }

}
