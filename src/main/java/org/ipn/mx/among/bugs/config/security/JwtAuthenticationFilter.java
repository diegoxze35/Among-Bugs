package org.ipn.mx.among.bugs.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ipn.mx.among.bugs.domain.dto.request.auth.LoginRequest;
import org.ipn.mx.among.bugs.domain.dto.response.auth.AuthResponse;
import org.ipn.mx.among.bugs.service.AuthService;
import org.ipn.mx.among.bugs.service.JwtService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.ipn.mx.among.bugs.service.JwtService.HEADER_STRING;
import static org.ipn.mx.among.bugs.service.JwtService.TOKEN_PREFIX;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final ObjectMapper objectMapper;
	private final JwtService jwtService;
	private final MessageSource messageSource;
	private final AuthService authService;
	private static final String CONTENT_TYPE = "application/json";
	private static final String LOGIN_URL = "/api/auth/login";
	private static final String REQUEST_KEY = "cachedLoginRequest";

	public JwtAuthenticationFilter(
			AuthenticationManager authenticationManager,
			ObjectMapper objectMapper,
			JwtService jwtService, MessageSource messageSource,
			AuthService authService
	) {
		super();
		setFilterProcessesUrl(LOGIN_URL);
		this.authenticationManager = authenticationManager;
		this.objectMapper = objectMapper;
		this.jwtService = jwtService;
		this.messageSource = messageSource;
		this.authService = authService;
	}

	@Override
	public Authentication attemptAuthentication(
			HttpServletRequest request,
			HttpServletResponse response
	) throws AuthenticationException {
		final LoginRequest loginRequest;
		try {
			loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
			request.setAttribute(REQUEST_KEY, loginRequest.email());
		} catch (Exception e) {
			throw new RuntimeException("Failed to get authentication data", e);
		}
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
				loginRequest.email(),
				loginRequest.password()
		);
		return authenticationManager.authenticate(authenticationToken);
	}

	@Override
	protected void successfulAuthentication(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain,
			Authentication authResult
	) throws IOException {
		User user = (User) authResult.getPrincipal();
		Collection<? extends GrantedAuthority> roles = user.getAuthorities();
		final String[] playerData = user.getUsername().split(",");
		final String playerId = playerData[0];
		final String playerUsername = playerData[1];
		Claims claims = Jwts
				.claims()
				.add("newEmail", playerUsername)
				.add("roles", objectMapper.writeValueAsString(roles))
				.build();
		final String token = jwtService.generateToken(claims, playerId);
		response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
		AuthResponse authResponse = new AuthResponse(token, null);
		response.getWriter().write(objectMapper.writeValueAsString(authResponse));
		response.setContentType(CONTENT_TYPE);
		response.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void unsuccessfulAuthentication(
			HttpServletRequest request,
			HttpServletResponse response,
			AuthenticationException failed
	) throws IOException {
		if (failed instanceof DisabledException) {
			final String playerEmail = (String) request.getAttribute(REQUEST_KEY);
			Locale locate = LocaleContextHolder.getLocale();
			final String message;
			if (authService.updateVerificationTokenByPlayerEmailIfExpired(playerEmail))
				message = messageSource.getMessage("auth.login.not.verified", null, locate);
			else
				message = messageSource.getMessage("auth.login.not.verified.yet", null, locate);
			response.getWriter().write("{\"message\":" + "\"" + message + "\"}");
			response.setContentType(CONTENT_TYPE);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		Map<String, String> body = new HashMap<>();
		body.put("error", failed.getLocalizedMessage());
		response.getWriter().write(objectMapper.writeValueAsString(body));
		response.setContentType(CONTENT_TYPE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	}

}
