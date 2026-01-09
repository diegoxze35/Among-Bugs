package org.ipn.mx.among.bugs.domain.entity.proyection;

import java.time.LocalDateTime;

public interface VerificationData {
	LocalDateTime getExpiryDate();
	String getUsername();
}
