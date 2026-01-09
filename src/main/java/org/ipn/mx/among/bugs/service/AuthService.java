package org.ipn.mx.among.bugs.service;

import org.ipn.mx.among.bugs.domain.dto.request.player.CreatePlayerRequest;
import org.ipn.mx.among.bugs.domain.dto.response.auth.RegisterResponse;
import org.ipn.mx.among.bugs.domain.dto.response.auth.TokenVerificationState;

public interface AuthService {
	RegisterResponse register(CreatePlayerRequest playerRequest);
	TokenVerificationState verifyAccount(String token);
	boolean updateVerificationTokenByPlayerEmailIfExpired(String playerEmail);
}
