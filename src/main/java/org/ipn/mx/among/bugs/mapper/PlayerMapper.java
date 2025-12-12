package org.ipn.mx.among.bugs.mapper;

import org.ipn.mx.among.bugs.domain.dto.request.player.CreatePlayerRequest;
import org.ipn.mx.among.bugs.domain.dto.response.player.PlayerResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;

public class PlayerMapper {

    public static PlayerResponse toDto(Player player) {
        return new PlayerResponse(player.getId(), player.getUsername(), player.getEmail());
    }

    public static Player toEntity(CreatePlayerRequest request) {
        return Player.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(request.passwordHash())
                .build();
    }

}
