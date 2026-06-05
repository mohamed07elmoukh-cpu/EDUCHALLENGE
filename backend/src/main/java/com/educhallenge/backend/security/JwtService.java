package com.educhallenge.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class JwtService {

	private final SecretKey signingKey;
	private final Duration expiration;

	public JwtService(
			@Value("${app.security.jwt.secret:educhallenge-super-secret-key-change-me-1234567890}") String jwtSecret,
			@Value("${app.security.jwt.expiration:PT12H}") Duration expiration
	) {
		this.signingKey = buildSigningKey(jwtSecret);
		this.expiration = expiration;
	}

	public String generateToken(AuthenticatedUserDetails userDetails) {
		Instant now = Instant.now();
		Instant expirationInstant = now.plus(expiration);

		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", userDetails.getUserId());
		claims.put("role", userDetails.getRole());
		claims.put("username", userDetails.getDisplayUsername());

		return Jwts.builder()
				.claims(claims)
				.subject(userDetails.getUsername())
				.issuedAt(Date.from(now))
				.expiration(Date.from(expirationInstant))
				.signWith(signingKey)
				.compact();
	}

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return StringUtils.hasText(username)
				&& username.equalsIgnoreCase(userDetails.getUsername())
				&& extractAllClaims(token).getExpiration().after(new Date());
	}

	public long getExpirationSeconds() {
		return expiration.toSeconds();
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private SecretKey buildSigningKey(String secret) {
		String sanitizedSecret = secret == null ? "" : secret.trim();

		if (sanitizedSecret.length() >= 32) {
			return Keys.hmacShaKeyFor(sanitizedSecret.getBytes(StandardCharsets.UTF_8));
		}

		byte[] decodedSecret = Decoders.BASE64.decode(sanitizedSecret);
		return Keys.hmacShaKeyFor(decodedSecret);
	}
}
