package org.ipn.mx.among.bugs.domain.dto.response.auth;

public record RegisterResponse(
		String email,
		String username,
		String message
) {
}
