package org.ipn.mx.among.bugs.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "verification_tokens")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {

    private static final byte EXPIRATION_TIME_IN_MINUTES = 15;

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
    public VerificationToken(String token, Player player) {
        this.token = token;
        this.player = player;
        this.expiryDate = LocalDateTime.now().plusMinutes(EXPIRATION_TIME_IN_MINUTES); //It Expires in 15 minutes
    }

}
