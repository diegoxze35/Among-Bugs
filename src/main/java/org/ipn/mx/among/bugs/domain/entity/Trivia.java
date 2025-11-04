package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Trivia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "creator_player_id") //Columna de llave foranea
	private Player player;

	@Column(nullable = false)
	private Integer scoreTarget;

	@Column(nullable = false, length = 50)
	private String title;

	@Column(length = 200)
	private String description;

    //Esto por que yo quiero (por ahora)
	@Column(nullable = false, columnDefinition = "boolean default false")
	protected Boolean isPublic; //Esta cosa es para indicar si es publico o privado xd

	@Lob
	@Column(columnDefinition = "LONGBLOB")
	private byte[] coverImage; //La imagen de la trivia

    @OneToMany(
            mappedBy = "trivia",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.REMOVE}
    )
    private List<Rating> ratings;

    @OneToOne(
            mappedBy = "trivia",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.REMOVE}
    )
    private Game game;

    //Relacion de muchos a muchos entre trivia y question
    @ManyToMany
    @JoinTable(
            name = "trivia_question",
            joinColumns = @JoinColumn(name = "trivia_id"),
            inverseJoinColumns = @JoinColumn(name="question_id")
    )
    private List<Question> questions;
}
