package org.ipn.mx.among.bugs.domain.entity.proyection;

public interface PlayerAuthData {
	Long getId();
	String getUsername();
	String getPasswordHash();
	Boolean getIsEnabled();
}
