package org.ipn.mx.among.bugs.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.player.UpdatePlayerRequest;
import org.ipn.mx.among.bugs.domain.dto.response.player.PlayerResponse;
import org.ipn.mx.among.bugs.domain.dto.response.player.UpdatedPlayerResponse;
import org.ipn.mx.among.bugs.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class PlayerController {

	private final PlayerService playerService;

	@GetMapping
	public ResponseEntity<PlayerResponse> getPlayerProfile(@AuthenticationPrincipal Long playerId) {
		PlayerResponse player = playerService.getPlayerProfile(playerId);
		return ResponseEntity.ok(player);
	}

	@PutMapping
	public ResponseEntity<UpdatedPlayerResponse> updateProfile(
			@AuthenticationPrincipal Long playerId,
			@Valid @RequestBody UpdatePlayerRequest request
	) {
		UpdatedPlayerResponse player = playerService.updateProfile(request, playerId);
		return ResponseEntity.ok(player);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePlayer(@AuthenticationPrincipal Long playerId) {
		playerService.deletePlayer(playerId);
	}

}
