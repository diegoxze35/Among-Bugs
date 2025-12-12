package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "players")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Player {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(unique = true, nullable = false, length = 25)
	private String username;
	@Column(unique = true, nullable = false, length = 50)
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

/*
 * {
 *      "username": "Manuel",
 *      "email": "Manue2@manue.com",
 *      "password": "S0m3H45H123",
 *      "trivia": [
 *          { ... }, { ... }
 *      ]
 * }
 * */
