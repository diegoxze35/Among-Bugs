package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.*;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

    @OneToMany(
            mappedBy = "player",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.REMOVE}
    )
    private List<Rating> ratings;

    @ManyToMany(mappedBy = "players")
    private List<Game> games;
}
