package org.ipn.mx.among.bugs.controller;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.ipn.mx.among.bugs.domain.dto.request.player.CreatePlayerRequest;
import org.ipn.mx.among.bugs.domain.dto.response.auth.RegisterResponse;
import org.ipn.mx.among.bugs.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
		/*
		* TODO:
		*  Por favor vean el archivo de propiedades ahí están definidas las propiedades
		*  frontend.email.verification.url.success
		*  frontend.email.verification.url.error
		*  frontend.email.verification.url.expired
		*  En el frontend hagan tres páginas, una donde se confirme que el usuarió verificó
		*  su cuenta exitosamente, otra donde ocurrió un error (el token para verificar su cuenta
		*  es invalido) y otra donde le diga que el token expiró (o puede ser solo una página con
		*  un estado, no sé, por ejemplo http://localhost:4200/verify?state={el_estado})
		*  el punto es que deben hacer las vistas, y modificar el archivo de propiedades
		*  para que redirija a la página correcta.
		* */
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
