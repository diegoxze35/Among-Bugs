package org.ipn.mx.among.bugs.domain.dto.request.player;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/*Same as CreatePlayerRequest, but these fields can be null*/
public record UpdatePlayerRequest(
		@Size(min = 5, max = 25)
		String newUsername,
		@Email
		String newEmail,
		@Size(min = 8)
		String newPassword
) {
}
