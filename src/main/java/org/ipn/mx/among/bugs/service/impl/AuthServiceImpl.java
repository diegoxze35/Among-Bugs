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
import org.ipn.mx.among.bugs.domain.entity.proyection.VerificationData;
import org.ipn.mx.among.bugs.mapper.PlayerMapper;
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

	@Value("${domain.base.url}")
	private String baseUrl;
	@Value("${security.verify.expiration-time-minutes}")
	private byte expirationTime;

	private final PlayerRepository playerRepository;
	private final MessageSource messageSource;
	private final VerificationTokenRepository verificationTokenRepository;
	private final PasswordEncoder passwordEncoder;
	private final EmailSenderService emailSenderService;

	@Transactional
	@Override
	public RegisterResponse register(CreatePlayerRequest playerRequest) {
		Player newPlayer = PlayerMapper.toEntity(playerRequest, passwordEncoder);
		Player savedPlayer = playerRepository.save(newPlayer);
		String emailVerificationToken = UUID.randomUUID().toString();
		VerificationToken verificationToken = VerificationToken.builder()
				.token(emailVerificationToken)
				.player(savedPlayer)
				.expiryTimeInMinutes(expirationTime)
				.build();
		verificationTokenRepository.save(verificationToken);
		final String savedEmail = savedPlayer.getEmail();
		final String savedUsername = savedPlayer.getUsername();
		final String verificationUrl = baseUrl + "/api/auth/verify?token=" + emailVerificationToken;
		Locale locale = LocaleContextHolder.getLocale();
		emailSenderService.sendVerificationEmail(savedEmail, savedUsername, locale, verificationUrl);
		final Object[] args = Collections.singletonList(savedEmail).toArray();
		final String message = messageSource.getMessage("email.verification.response", args, locale);
		return new RegisterResponse(savedEmail, savedUsername, message);
	}

	@Transactional
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

	@Transactional
	@Override
	public boolean updateVerificationTokenByPlayerEmailIfExpired(String playerEmail) {
		VerificationData verificationData = verificationTokenRepository.findExpiryDateByPlayerEmail(playerEmail);
		LocalDateTime expiryDate = verificationData.getExpiryDate();
		if (expiryDate.isBefore(LocalDateTime.now())) {
			Locale locale = LocaleContextHolder.getLocale();
			final String playerUsername = verificationData.getUsername();
			final String emailVerificationToken = UUID.randomUUID().toString();
			verificationTokenRepository.updateVerificationTokenByPlayerEmail(
					emailVerificationToken, LocalDateTime.now().plusMinutes(15), playerEmail
			);
			final String verificationUrl = baseUrl + "/api/auth/verify?token=" + emailVerificationToken;
			emailSenderService.sendVerificationEmail(playerEmail, playerUsername, locale, verificationUrl);
			return true;
		} else {
			return false;
		}
	}

}
