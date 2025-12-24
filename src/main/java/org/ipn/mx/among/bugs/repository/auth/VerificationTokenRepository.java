package org.ipn.mx.among.bugs.repository.auth;

import java.time.LocalDateTime;
import java.util.Optional;
import org.ipn.mx.among.bugs.domain.entity.VerificationToken;
import org.ipn.mx.among.bugs.domain.entity.proyection.TokenData;
import org.ipn.mx.among.bugs.domain.entity.proyection.VerificationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	Optional<TokenData> findByToken(String token);

	@Query(value = "SELECT vt.expiry_date, p.username FROM verification_tokens vt INNER JOIN players p" +
			" on p.id = vt.player_id WHERE p.email = :email", nativeQuery = true)
	VerificationData findExpiryDateByPlayerEmail(String email);

	@Modifying
	@Query(value = "UPDATE verification_tokens SET expiry_date = :expiryTime, token = :token" +
			" FROM verification_tokens vt INNER JOIN public.players p on p.id = vt.player_id " +
			"WHERE p.email = :playerEmail", nativeQuery = true)
	void updateVerificationTokenByPlayerEmail(String token, LocalDateTime expiryTime, String playerEmail);

}
