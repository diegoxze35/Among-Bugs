package org.ipn.mx.among.bugs.exception;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	private final MessageSource messageSource;
	private static final String KEY = "message";

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
		return errors;
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(DataIntegrityViolationException.class)
	public Map<String, String> handleDuplicateEntry(DataIntegrityViolationException ex) {
		Map<String, String> errors = new HashMap<>();
		Locale locale = LocaleContextHolder.getLocale();
		if (ex.getCause() instanceof ConstraintViolationException constraintEx) {
			String constraintName = constraintEx.getConstraintName();
			if ("uk_players_username".equals(constraintName)) {
				errors.put(KEY, messageSource.getMessage("auth.login.username.exist", null, locale));
			} else if ("uk_players_email".equals(constraintName)) {
				errors.put(KEY, messageSource.getMessage("auth.login.email.exist", null, locale));
			} else {
				errors.put(KEY, messageSource.getMessage("auth.login.error", null, locale));
			}
		} else {
			errors.put(KEY, messageSource.getMessage("auth.login.error", null, locale));
		}
		return errors;
	}

	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(ResourceNotFoundException.class)
	public Map<String, String> handleNotFoundException(ResourceNotFoundException ex) {
		return Collections.singletonMap(KEY, ex.getMessage());
	}

}
