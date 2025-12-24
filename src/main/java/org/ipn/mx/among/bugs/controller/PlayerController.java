package org.ipn.mx.among.bugs.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.player.CreatePlayerRequest;
import org.ipn.mx.among.bugs.domain.dto.response.player.PlayerResponse;
import org.ipn.mx.among.bugs.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlayerController {

	/*private final PlayerService playerService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PlayerResponse createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
		return playerService.createPlayer(request);
	}

	@PutMapping(value = "/{playerId}")
	public PlayerResponse updateProfile(@Valid @RequestBody CreatePlayerRequest request, @PathVariable Long playerId) {
		return playerService.updateProfile(request, playerId);
	}

	@DeleteMapping( value = "/{playerId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePlayer(@PathVariable Long playerId) {
		playerService.deletePlayer(playerId);
	}*/

}
