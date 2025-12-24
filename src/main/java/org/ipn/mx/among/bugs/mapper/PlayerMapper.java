package org.ipn.mx.among.bugs.mapper;

import org.ipn.mx.among.bugs.domain.dto.request.player.CreatePlayerRequest;
import org.ipn.mx.among.bugs.domain.dto.response.player.PlayerResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PlayerMapper {

	public static PlayerResponse toDto(Player player) {
		return new PlayerResponse(player.getId(), player.getUsername(), player.getEmail());
	}

	public static Player toEntity(CreatePlayerRequest request, PasswordEncoder encoder) {
		return Player.builder()
				.username(request.username())
				.email(request.email())
				.passwordHash(encoder.encode(request.password()))
				.build();
	}

}
