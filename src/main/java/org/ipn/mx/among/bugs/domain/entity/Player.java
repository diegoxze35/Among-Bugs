package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Table(
		name = "players",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_players_username", columnNames = "username"),
				@UniqueConstraint(name = "uk_players_email", columnNames = "email")
		}
)
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Player {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, length = 25)
	private String username;
	@Column(nullable = false)
	private Boolean isEnabled = false;
	@Column(nullable = false, length = 50)
	private String email;
	@Column(nullable = false)
	private String passwordHash;
	@OneToMany(
			targetEntity = Trivia.class,
			mappedBy = "player",
			fetch = FetchType.LAZY,
			cascade = {CascadeType.MERGE, CascadeType.REMOVE, CascadeType.REFRESH, CascadeType.DETACH},
			orphanRemoval = true
	)
	private Set<Trivia> trivia; /*This is a set because a player can't have the same Trivia record*/

	@Builder
	public Player(String username, String email, String passwordHash) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
	}

}
