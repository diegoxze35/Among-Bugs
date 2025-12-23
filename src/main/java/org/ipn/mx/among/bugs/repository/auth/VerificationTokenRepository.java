package org.ipn.mx.among.bugs.repository.auth;

import java.util.Optional;
import org.ipn.mx.among.bugs.domain.entity.VerificationToken;
import org.ipn.mx.among.bugs.domain.entity.proyection.TokenData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

	Optional<TokenData> findByToken(String token);

}
