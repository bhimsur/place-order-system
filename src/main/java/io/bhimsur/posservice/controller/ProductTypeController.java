package io.bhimsur.posservice.controller;

import io.bhimsur.posservice.dto.ApiResponse;
import io.bhimsur.posservice.dto.ProductTypeDto;
import io.bhimsur.posservice.service.ProductTypeService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/product-types")
@Validated
public class ProductTypeController {
	private final ProductTypeService productTypeService;

	@GetMapping
	public ResponseEntity<?> findAll(
			@RequestParam @NotNull Boolean deleted,
			Pageable pageable
	) {
		Page<ProductTypeDto> response = productTypeService.findAll(deleted, pageable);
		return ApiResponse.success(response);
	}
}