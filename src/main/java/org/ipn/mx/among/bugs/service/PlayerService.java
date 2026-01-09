package org.ipn.mx.among.bugs.service;

import org.ipn.mx.among.bugs.domain.dto.request.player.UpdatePlayerRequest;
import org.ipn.mx.among.bugs.domain.dto.response.player.PlayerResponse;
import org.ipn.mx.among.bugs.domain.dto.response.player.UpdatedPlayerResponse;

public interface PlayerService {
	PlayerResponse getPlayerProfile(Long playerId);
	UpdatedPlayerResponse updateProfile(UpdatePlayerRequest newPlayer, Long playerId);
	void deletePlayer(Long playerId);
}
