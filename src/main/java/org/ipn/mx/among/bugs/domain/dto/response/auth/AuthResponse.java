package org.ipn.mx.among.bugs.domain.dto.response.auth;

public record AuthResponse(
		String token,
		String refreshToken
) {
}
