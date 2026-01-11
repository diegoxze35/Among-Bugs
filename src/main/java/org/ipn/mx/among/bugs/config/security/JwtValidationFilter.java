package org.ipn.mx.among.bugs.config.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.ipn.mx.among.bugs.service.JwtService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.ipn.mx.among.bugs.service.JwtService.HEADER_STRING;
import static org.ipn.mx.among.bugs.service.JwtService.TOKEN_PREFIX;

public class JwtValidationFilter extends BasicAuthenticationFilter {

	private final JwtService jwtService;
	private final ObjectMapper objectMapper;

	public JwtValidationFilter(AuthenticationManager authenticationManager, ObjectMapper objectMapper, JwtService jwtService) {
		super(authenticationManager);
		this.objectMapper = objectMapper;
		this.jwtService = jwtService;
	}

	private static class SimpleGrantedAuthorityJsonCreator {
		@JsonCreator
		public SimpleGrantedAuthorityJsonCreator(@JsonProperty("authority") String role) {
		}
	}

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain
	) throws ServletException, IOException {

		final String authHeader = request.getHeader(HEADER_STRING);

		if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		final String jwt = authHeader.substring(7);
		Claims claims;
		try {
			claims = jwtService.extractAllClaims(jwt);
		} catch (JwtException e) {
			SecurityContextHolder.clearContext();
			filterChain.doFilter(request, response);
			return;
		}
		final long playerId = Long.parseLong(claims.getSubject());
		Object authorities = claims.get("roles");
		Collection<? extends GrantedAuthority> roles = Arrays.asList(
				objectMapper
						.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class)
						.readValue(authorities.toString().getBytes(), SimpleGrantedAuthority[].class)
		);
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				playerId, null, roles
		);
		SecurityContextHolder.getContext().setAuthentication(authToken);
		filterChain.doFilter(request, response);
	}
}
