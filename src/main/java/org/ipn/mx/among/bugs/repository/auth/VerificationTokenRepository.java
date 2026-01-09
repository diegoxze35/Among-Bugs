package org.ipn.mx.among.bugs.repository.auth;

import org.ipn.mx.among.bugs.domain.entity.VerificationToken;
import org.ipn.mx.among.bugs.domain.entity.proyection.TokenData;
import org.ipn.mx.among.bugs.domain.entity.proyection.VerificationData;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {

	Optional<TokenData> findByToken(String token);

	@Query(value = "SELECT vt.expiry_date, p.username FROM verification_tokens vt INNER JOIN players p" +
			" ON p.id = vt.player_id WHERE p.email = :email", nativeQuery = true)
	VerificationData findExpiryDateByPlayerEmail(String email);

	@Modifying
	@Query(value = "UPDATE verification_tokens SET expiry_date = :expiryTime, token = :token" +
			" FROM verification_tokens vt INNER JOIN public.players p ON p.id = vt.player_id " +
			"WHERE p.email = :playerEmail", nativeQuery = true)
	void updateVerificationTokenByPlayerEmail(String token, LocalDateTime expiryTime, String playerEmail);

}
