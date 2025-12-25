package org.ipn.mx.among.bugs.domain.dto.response.player;

public record UpdatedPlayerResponse(
		String username,
		String email,
		boolean passwordUpdated
) {
}
