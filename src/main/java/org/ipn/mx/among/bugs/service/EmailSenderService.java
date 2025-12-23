package org.ipn.mx.among.bugs.service;

import java.util.Locale;
import org.ipn.mx.among.bugs.domain.dto.request.player.CreatePlayerRequest;

public interface EmailSenderService {
	void sendVerificationEmail(CreatePlayerRequest request, Locale locale, String verificationUrl);
}
