package org.ipn.mx.among.bugs.repository.player;

import java.util.Optional;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.entity.proyection.PlayerAuthData;
import org.ipn.mx.among.bugs.domain.entity.proyection.PlayerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlayerRepository extends JpaRepository<Player, Long> {

	Optional<PlayerAuthData> findByEmail(String email);

	PlayerData findPlayerDataById(Long id);

	@Modifying
    @Query("UPDATE Player p SET p.isEnabled = true WHERE p.id = " +
           "(SELECT vt.player.id FROM VerificationToken vt WHERE vt.token = :token)")
    void enablePlayerByToken(@Param("token") String token);

}
