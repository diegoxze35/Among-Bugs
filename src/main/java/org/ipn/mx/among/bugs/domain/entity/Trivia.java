package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

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
			String title,
			String description,
			boolean isPublic
	) {
		this.player = player;
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
