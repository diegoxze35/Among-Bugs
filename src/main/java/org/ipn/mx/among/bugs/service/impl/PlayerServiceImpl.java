package org.ipn.mx.among.bugs.service.impl;

import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.player.UpdatePlayerRequest;
import org.ipn.mx.among.bugs.domain.dto.response.player.PlayerResponse;
import org.ipn.mx.among.bugs.domain.dto.response.player.UpdatedPlayerResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.entity.proyection.PlayerData;
import org.ipn.mx.among.bugs.mapper.PlayerMapper;
import org.ipn.mx.among.bugs.repository.player.PlayerRepository;
import org.ipn.mx.among.bugs.service.PlayerService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

	private final PlayerRepository playerRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public PlayerResponse getPlayerProfile(Long playerId) {
		PlayerData playerData = playerRepository.findPlayerDataById(playerId);
		return new PlayerResponse(playerData.getUsername(), playerData.getEmail());
	}

	@Override
	@Transactional(rollbackFor = DataIntegrityViolationException.class)
	public UpdatedPlayerResponse updateProfile(UpdatePlayerRequest newPlayer, Long playerId) {
		Player playerToUpdate = playerRepository.findById(playerId).orElseThrow();
		final String newUsername = newPlayer.newUsername();
		final String newEmail = newPlayer.newEmail();
		final String newPassword = newPlayer.newPassword();
		boolean passwordUpdated = false;
		if (newUsername != null && !newUsername.equals(playerToUpdate.getUsername()))
			playerToUpdate.setUsername(newUsername);
		if (newEmail != null && !newEmail.equals(playerToUpdate.getEmail()))
			playerToUpdate.setEmail(newEmail);
		if (newPassword != null) {
			final String newPasswordHash = passwordEncoder.encode(newPassword);
			playerToUpdate.setPasswordHash(newPasswordHash);
			passwordUpdated = true;
		}
		Player updated = playerRepository.save(playerToUpdate);
		return PlayerMapper.toDto(updated, passwordUpdated);
	}

	@Override
	@Transactional
	public void deletePlayer(Long playerId) {
		playerRepository.deleteById(playerId);
	}

}
