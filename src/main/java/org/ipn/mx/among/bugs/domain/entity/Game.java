package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idGame;

    @Column(nullable = false)
    private LocalDateTime finishedAt;

    @OneToOne
    @JoinColumn(name = "trivia_id", nullable = false, unique = true)
    private Trivia trivia;

    @ManyToMany
    @JoinTable(
            name = "game_player",
            joinColumns = @JoinColumn(name="id_game"),
            inverseJoinColumns = @JoinColumn(name = "id_player")
    )
    private List<Player> players;
}
