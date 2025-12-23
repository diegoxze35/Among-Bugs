package org.ipn.mx.among.bugs.domain.dto.request.player;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePlayerRequest(
		@Size(min = 5, max = 25)
		@NotNull
		String username,
		@Email
		@NotNull
		String email,
		@NotNull
		String password
) {
}
