package io.bhimsur.posservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddToCartRequest implements Serializable {

	@Positive
	private Long productId;
	@NotBlank
	private String username;
	@Positive
	private Integer quantity;
}