package io.bhimsur.posservice.controller.exception;

import io.bhimsur.posservice.dto.ApiResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler {

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity<?> handle(Exception e) {
		return ApiResponse.badRequest(e.getMessage());
	}

	@ExceptionHandler(ApiException.class)
	@ResponseBody
	public ResponseEntity<?> handleApiException(ApiException e) {
		return ApiResponse.generalError(e.getHttpStatus(), e.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
		return ApiResponse.badRequest(ex.getBindingResult().getFieldErrors()
				.stream().map(error -> error.getField() + ":" + error.getDefaultMessage()).collect(Collectors.toList()));
    }
}