package org.ipn.mx.among.bugs.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.entity.proyection.PlayerCredentials;
import org.ipn.mx.among.bugs.repository.player.PlayerRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

	private final PlayerRepository playerRepository;
	private final MessageSource messageSource;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		PlayerCredentials playerCredentials = playerRepository.findByEmail(username).orElseThrow(() -> {
			Locale locale = LocaleContextHolder.getLocale();
			final String message = messageSource.getMessage("auth.login.not.found", null, locale);
			return new UsernameNotFoundException(message);
		});
		List<GrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority("ROLE_PLAYER"));
		return new User(
				joinPlayerData(playerCredentials),
				playerCredentials.getPasswordHash(),
				playerCredentials.getIsEnabled(),
				true,
				true,
				true,
				roles
		);
	}

	private String joinPlayerData(PlayerCredentials p) {
		return p.getId().toString() + ',' + p.getUsername();
	}

}
