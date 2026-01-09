package org.ipn.mx.among.bugs.service.impl;

import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.service.EmailSenderService;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {

	private final JavaMailSender mailSender;
	private final MessageSource messageSource;

	@Async
	@Override
	public void sendVerificationEmail(String playerEmail, String playerUsername, Locale locale, String verificationUrl) {
		SimpleMailMessage message = new SimpleMailMessage();
		final String subject = messageSource.getMessage("email.verification.subject", null, locale);
		final Object[] args = List.of(playerUsername, verificationUrl).toArray();
		final String content = messageSource.getMessage("email.verification.text", args, locale);
		message.setTo(playerEmail);
		message.setSubject(subject);
		message.setText(content);
		mailSender.send(message);

	}
}
