package org.ipn.mx.among.bugs.domain.entity.proyection;

import java.time.LocalDateTime;

public interface TokenData {
	String getToken();
	LocalDateTime getExpiryDate();
}
