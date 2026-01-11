package org.ipn.mx.among.bugs.mapper;

import org.ipn.mx.among.bugs.domain.dto.request.player.CreatePlayerRequest;
import org.ipn.mx.among.bugs.domain.dto.response.player.UpdatedPlayerResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PlayerMapper {

	private PlayerMapper() {}

	public static UpdatedPlayerResponse toDto(Player player, boolean passwordUpdated) {
		return new UpdatedPlayerResponse(player.getUsername(), player.getEmail(), passwordUpdated);
	}

	public static Player toEntity(CreatePlayerRequest request, PasswordEncoder encoder) {
		return Player.builder()
				.username(request.username())
				.email(request.email())
				.passwordHash(encoder.encode(request.password()))
				.build();
	}

}
