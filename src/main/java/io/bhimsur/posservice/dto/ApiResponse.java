package io.bhimsur.posservice.dto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ApiResponse<T>(
		Integer code,
		String message,
		T data
) {
	public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
		ApiResponse<T> response = new ApiResponse<>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), data);
		return ResponseEntity.ok(response);
	}

	public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus httpStatus, T data) {
		ApiResponse<T> response = new ApiResponse<>(httpStatus.value(), httpStatus.getReasonPhrase(), data);
		return ResponseEntity.status(httpStatus).body(response);
	}

	public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus httpStatus) {
		ApiResponse<T> response = new ApiResponse<>(httpStatus.value(), httpStatus.getReasonPhrase(), null);
		return ResponseEntity.status(httpStatus).body(response);
	}

	public static <T> ResponseEntity<ApiResponse<T>> generalError(HttpStatus httpStatus, T data) {
		ApiResponse<T> response = new ApiResponse<>(httpStatus.value(), httpStatus.getReasonPhrase(), data);
		return ResponseEntity.status(httpStatus.value()).body(response);
	}

	public static <T> ResponseEntity<ApiResponse<T>> badRequest(T data) {
		ApiResponse<T> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), data);
		return ResponseEntity.badRequest().body(response);
	}
}