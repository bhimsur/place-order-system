package io.bhimsur.posservice.controller;

import io.bhimsur.posservice.dto.ApiResponse;
import io.bhimsur.posservice.dto.OrderDto;
import io.bhimsur.posservice.dto.PlaceOrderRequest;
import io.bhimsur.posservice.dto.PlaceOrderResponse;
import io.bhimsur.posservice.service.OrderService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {
	private final OrderService orderService;

	@PostMapping(
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<?> placeOrder(
			@RequestBody PlaceOrderRequest request
	) throws Exception {
		PlaceOrderResponse response = orderService.placeOrder(request);
		return ApiResponse.success(HttpStatus.CREATED, response);
	}

	@GetMapping(
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<?> myOrder(
			@RequestParam @NotBlank String username,
			Pageable pageable
	) {
		Page<OrderDto> response = orderService.myOrder(username, pageable);
		return ApiResponse.success(response);
	}
}