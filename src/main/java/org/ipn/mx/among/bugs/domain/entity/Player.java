package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
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
			mappedBy = "player",
			fetch = FetchType.LAZY,
			/*TODO(Finding out how to remove on cascade based on boolean attribute)*/
			cascade = {CascadeType.REMOVE}
	)
	private List<Trivia> trivia;
}

/*
 * {
 *      "username": "Manue",
 *      "email": "Manue2@manue.com",
 *      "password": "manue123",
 *      "trivia": [
 *          { ... }, { ... }
 *      ]
 * }
 * */
