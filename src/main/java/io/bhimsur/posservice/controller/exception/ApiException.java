package io.bhimsur.posservice.controller.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
	private static final long serialVersionUID = -3303518302920463234L;

	private final HttpStatus httpStatus;

	public ApiException(String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus = httpStatus;
	}

	public ApiException(HttpStatus httpStatus) {
		super(httpStatus.getReasonPhrase());
		this.httpStatus = httpStatus;
	}
}