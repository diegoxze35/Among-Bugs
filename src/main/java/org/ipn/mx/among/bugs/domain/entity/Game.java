package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CurrentTimestamp;

@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@Entity
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@CurrentTimestamp
	@Column(nullable = false)
	private LocalDateTime finishedAt;

	@OneToOne(
			targetEntity = Trivia.class,
			fetch = FetchType.EAGER,
			optional = false
			/*No cascade, this table is just a logbook*/
	)
	private Trivia trivia;

	@ManyToMany(
			targetEntity = Player.class,
			fetch = FetchType.LAZY
			/*No cascade, this table is just a logbook*/
	)
	@JoinTable(
			name = "game_player",
			joinColumns = @JoinColumn(name = "player_id"),
			inverseJoinColumns = @JoinColumn(name = "game_id")
	)
	private Set<Player> players;

}
