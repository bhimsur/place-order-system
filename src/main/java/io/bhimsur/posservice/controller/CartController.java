package io.bhimsur.posservice.controller;

import io.bhimsur.posservice.dto.AddToCartRequest;
import io.bhimsur.posservice.dto.CartResponse;
import io.bhimsur.posservice.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/carts")
@Validated
public class CartController {
	private final CartService cartService;

	@GetMapping
	public ResponseEntity<?> findByUsername(
			@RequestParam @NotBlank String username,
			Sort sort
	) throws Exception {
		CartResponse response = cartService.findCartByUsername(username, sort);
		return ResponseEntity.ok(response);
	}

	@PostMapping
	public ResponseEntity<?> addToCart(
			@RequestBody @Valid AddToCartRequest request
	) throws Exception {
		Boolean response = cartService.addToCart(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}


}