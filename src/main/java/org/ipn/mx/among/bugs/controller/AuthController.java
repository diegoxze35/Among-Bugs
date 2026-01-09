package org.ipn.mx.among.bugs.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.player.CreatePlayerRequest;
import org.ipn.mx.among.bugs.domain.dto.response.auth.RegisterResponse;
import org.ipn.mx.among.bugs.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService service;

	@Value("${frontend.email.verification.url.success}")
	private String successUrl;
	@Value("${frontend.email.verification.url.error}")
	private String errorUrl;
	@Value("${frontend.email.verification.url.expired}")
	private String expiredUrl;

	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(@RequestBody @Valid CreatePlayerRequest request) {
		RegisterResponse response = service.register(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/verify")
	public ResponseEntity<Void> verifyAccount(@RequestParam("token") String token) {
		URI uri = switch (service.verifyAccount(token)) {
			case VALID -> URI.create(successUrl);
			case INVALID -> URI.create(errorUrl);
			case EXPIRED -> URI.create(expiredUrl);
		};
		return ResponseEntity.status(HttpStatus.FOUND)
				.location(uri)
				.build();
	}

}
