package org.ipn.mx.among.bugs.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtService {

	@Value("${security.jwt.expiration-time}")
	private long jwtExpiration;
	public static final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";

	public String generateToken(Claims extraClaims, String playerId) {
		return Jwts.builder()
				.claims(extraClaims)
				.subject(playerId)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(SECRET_KEY)
				.compact();
	}

	public Claims extractAllClaims(String token) throws JwtException {
		return Jwts.parser()
				.verifyWith(SECRET_KEY)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

}
