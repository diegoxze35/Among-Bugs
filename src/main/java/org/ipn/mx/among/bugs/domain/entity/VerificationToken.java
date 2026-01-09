package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "verification_tokens")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @OneToOne(targetEntity = Player.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "player_id")
    private Player player;

    private LocalDateTime expiryDate;

	@Builder
    public VerificationToken(String token, Player player, byte expiryTimeInMinutes) {
        this.token = token;
        this.player = player;
        this.expiryDate = LocalDateTime.now().plusMinutes(expiryTimeInMinutes);
    }

}
