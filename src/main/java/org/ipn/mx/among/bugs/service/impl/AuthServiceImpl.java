package org.ipn.mx.among.bugs.service.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.player.CreatePlayerRequest;
import org.ipn.mx.among.bugs.domain.dto.response.auth.RegisterResponse;
import org.ipn.mx.among.bugs.domain.dto.response.auth.TokenVerificationState;
import org.ipn.mx.among.bugs.domain.entity.Player;
import org.ipn.mx.among.bugs.domain.entity.VerificationToken;
import org.ipn.mx.among.bugs.domain.entity.proyection.TokenData;
import org.ipn.mx.among.bugs.repository.auth.VerificationTokenRepository;
import org.ipn.mx.among.bugs.repository.player.PlayerRepository;
import org.ipn.mx.among.bugs.service.AuthService;
import org.ipn.mx.among.bugs.service.EmailSenderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final PlayerRepository playerRepository;
	@Value("${domain.base.url}")
	private String baseUrl;

	private final MessageSource messageSource;
	private final PlayerRepository repository;
	private final VerificationTokenRepository verificationTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailSenderService emailSenderService;

	@Override
	public RegisterResponse register(CreatePlayerRequest playerRequest) {
		Player newPlayer = Player.builder()
				.username(playerRequest.username())
				.email(playerRequest.email())
				.passwordHash(passwordEncoder.encode(playerRequest.password()))
				.build();
		Player savedPlayer = repository.save(newPlayer);
		String emailVerificationToken = UUID.randomUUID().toString();
		VerificationToken verificationToken = VerificationToken.builder()
				.token(emailVerificationToken)
				.player(savedPlayer)
				.build();
		verificationTokenRepository.save(verificationToken);
		final String savedEmail = savedPlayer.getEmail();
		final String savedUsername = savedPlayer.getUsername();
		final String verificationUrl = baseUrl + "/api/auth/verify?token=" + emailVerificationToken;
		Locale locale = LocaleContextHolder.getLocale();
		emailSenderService.sendVerificationEmail(playerRequest, locale, verificationUrl);
		final String message = messageSource.getMessage("email.verification.response", Collections.singletonList(savedEmail).toArray(), locale);
		return new RegisterResponse(savedEmail, savedUsername, message);
	}

	@Transactional()
	@Override
	public TokenVerificationState verifyAccount(String token) {
		Optional<TokenData> optionalToken = verificationTokenRepository.findByToken(token);
		if (optionalToken.isEmpty()) {
			return TokenVerificationState.INVALID;
		}
		TokenData verificationToken = optionalToken.get();
		if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
			return TokenVerificationState.EXPIRED;
		}
		playerRepository.enablePlayerByToken(verificationToken.getToken());
		return TokenVerificationState.VALID;
	}

}
