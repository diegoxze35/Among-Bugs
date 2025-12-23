package org.ipn.mx.among.bugs.domain.dto.request.auth;

import jakarta.validation.constraints.Email;

public record LoginRequest(
		@Email String username,
		String password
) {
}
