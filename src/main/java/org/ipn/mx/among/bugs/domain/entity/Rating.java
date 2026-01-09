package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Table(name = "ratings")
@Entity
@Getter
@Setter
@NoArgsConstructor
@Check(constraints = "score >= 0 AND score <= 5")
public class Rating {
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
	@Column(nullable = false, columnDefinition = "SMALLINT")
	private Short score;
	@Column(columnDefinition = "TEXT")
	private String comment;
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Builder
	public Rating(Player player, Trivia trivia, Short score, String comment) {
		this.player = player;
		this.trivia = trivia;
		this.score = score;
		this.comment = comment;
	}
}
