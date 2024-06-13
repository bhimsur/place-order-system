package io.bhimsur.posservice.controller;

import io.bhimsur.posservice.dto.ApiResponse;
import io.bhimsur.posservice.dto.ProductDto;
import io.bhimsur.posservice.dto.SaveProductRequest;
import io.bhimsur.posservice.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {
	private final ProductService productService;

	@GetMapping
	public ResponseEntity<?> findAll(
			@RequestParam(required = false) String type,
			@RequestParam(required = false) String name,
			@RequestParam Boolean deleted,
			Pageable pageable
	) {
		Page<ProductDto> response = productService.findAll(type, name, deleted, pageable);
		return ApiResponse.success(response);
	}

	@PostMapping
	public ResponseEntity<?> save(
			@RequestBody @Valid SaveProductRequest request
	) throws Exception {
		ProductDto response = productService.saveProduct(null, request);
		return ApiResponse.success(HttpStatus.CREATED, response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(
			@RequestBody @Valid SaveProductRequest request,
			@PathVariable @Positive Long id
	) throws Exception {
		ProductDto response = productService.saveProduct(id, request);
		return ApiResponse.success(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(
			@PathVariable @Positive Long id
	) {
		productService.deleteProduct(id);
		return ApiResponse.success(HttpStatus.NO_CONTENT);
	}
}