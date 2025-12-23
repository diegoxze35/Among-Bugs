package org.ipn.mx.among.bugs.domain.entity.proyection;

public interface PlayerCredentials {
	Long getId();
	String getUsername();
	String getPasswordHash();
	Boolean getIsEnabled();
}
