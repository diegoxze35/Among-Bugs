package org.ipn.mx.among.bugs.service;

import java.util.Locale;

public interface EmailSenderService {
	void sendVerificationEmail(String playerEmail, String playerUsername, Locale locale, String verificationUrl);
}
