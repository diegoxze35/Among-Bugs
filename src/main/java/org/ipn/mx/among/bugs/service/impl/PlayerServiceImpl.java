package org.ipn.mx.among.bugs.service.impl;

import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.player.CreatePlayerRequest;
import org.ipn.mx.among.bugs.domain.dto.response.player.PlayerResponse;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.mapper.PlayerMapper;
import org.ipn.mx.among.bugs.repository.player.PlayerRepository;
import org.ipn.mx.among.bugs.service.PlayerService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

	private final PlayerRepository playerRepository;
	private final JavaMailSender mailSender;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public PlayerResponse createPlayer(CreatePlayerRequest player) {
		Player playerEntity = PlayerMapper.toEntity(player);
		Player savedPlayer = playerRepository.save(playerEntity);
		PlayerResponse response = PlayerMapper.toDto(savedPlayer);
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(response.email());
        message.setSubject("Among Bugs - Account Created!");
        message.setText("Your account has been created successfully.");
		mailSender.send(message);
		return response;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public PlayerResponse updateProfile(CreatePlayerRequest newPlayer, Long playerId) {
		Player playerToUpdate = playerRepository.findById(playerId).orElseThrow();
		playerToUpdate.setUsername(newPlayer.username());
		playerToUpdate.setEmail(newPlayer.email());
		playerToUpdate.setPasswordHash(newPlayer.passwordHash());
		Player updated = playerRepository.save(playerToUpdate);
		return PlayerMapper.toDto(updated);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deletePlayer(Long playerId) {
		playerRepository.deleteById(playerId);
	}

}
