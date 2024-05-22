package io.bhimsur.posservice.controller.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handle(Exception e) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", HttpStatus.BAD_REQUEST.value());
		map.put("message", e.getMessage());
		map.put("errors", null);

		return ResponseEntity.badRequest().body(map);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> map = new HashMap<>();
		map.put("status", HttpStatus.BAD_REQUEST.value());
		map.put("message", "validation error");
		map.put("errors", ex.getBindingResult().getFieldErrors()
				.stream().map(error -> error.getField() + ":" + error.getDefaultMessage()).collect(Collectors.toList()));
        return ResponseEntity.badRequest().body(map);
    }
}