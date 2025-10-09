package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class Trivia {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne
	@JoinColumn(name = "creator_player_id")
	private Player player;
	@Column(nullable = false)
	private Integer targetScore;
	@Column(nullable = false, length = 50)
	private String title;
	@Column(length = 200)
	private String description;
	@Column(nullable = false, columnDefinition = "boolean default false")
	protected Boolean isPublic;
	@Lob
	@Column(columnDefinition = "bytea")
	private byte[] coverImage;

}
